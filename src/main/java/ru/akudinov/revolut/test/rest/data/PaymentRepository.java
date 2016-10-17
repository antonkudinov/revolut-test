package ru.akudinov.revolut.test.rest.data;

import org.glassfish.hk2.extras.interception.Intercepted;
import ru.akudinov.revolut.test.rest.exceptions.AlreadyHasIdentifierException;
import ru.akudinov.revolut.test.rest.exceptions.ApplicationException;
import ru.akudinov.revolut.test.rest.exceptions.EntityNotFoundException;
import ru.akudinov.revolut.test.rest.model.Account;
import ru.akudinov.revolut.test.rest.model.Payment;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.ws.rs.PathParam;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static javax.persistence.LockModeType.PESSIMISTIC_WRITE;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static ru.akudinov.revolut.test.rest.data.TransactionalRunner.runInTransaction;

@Intercepted
public class PaymentRepository {
    public static final String JAVAX_PERSISTENCE_LOCK_TIMEOUT = "javax.persistence.lock.timeout";

    @Inject
    private EntityManager em;

    public Payment create(Payment payment) {
        return runInTransaction(em, () -> {

            checkPayment(payment);

            payment.setDate(new Date());

            // set timeout for pessimistic lock
            em.setProperty(JAVAX_PERSISTENCE_LOCK_TIMEOUT, TimeUnit.SECONDS.toMillis(1));

            final Account srcAccount;
            final Account dstAccount;
            if (payment.getWithdrawalAccountNumber() < payment.getDepositAccountNumber()) {
                srcAccount = getWithdrawalAccount(payment);
                dstAccount = getAccount(payment.getDepositAccountNumber(), "deposit");
            } else if (payment.getWithdrawalAccountNumber() > payment.getDepositAccountNumber()) {
                // revert the lock order of the bank accounts to avoid temporary dead locking
                dstAccount = getAccount(payment.getDepositAccountNumber(), "deposit");
                srcAccount = getWithdrawalAccount(payment);
            } else {
                throw new ApplicationException("Withdrawal and deposit accounts are equals", BAD_REQUEST);
            }

            srcAccount.setBalance(srcAccount.getBalance().subtract(payment.getAmount()));
            dstAccount.setBalance(dstAccount.getBalance().add(payment.getAmount()));

            em.merge(srcAccount);
            em.merge(dstAccount);

            em.persist(payment);
            return payment;
        });
    }

    public Payment read(@PathParam("id") long id) {
        return runInTransaction(em, () -> {
            final Payment payment = em.find(Payment.class, id);
            if (payment == null) {
                throw new EntityNotFoundException();
            }
            return payment;
        });
    }

    private void checkPayment(Payment payment) {
        if (payment.getId() != null) {
            throw new AlreadyHasIdentifierException();
        }
        if (payment.getAmount() == null) {
            throw new ApplicationException("Payment amount is null", BAD_REQUEST);
        }
    }

    private Account getWithdrawalAccount(Payment payment) {
        final Account account = getAccount(payment.getWithdrawalAccountNumber(), "withdrawal");
        if (account.getBalance().compareTo(payment.getAmount()) < 0) {
            throw new ApplicationException("Withdrawal account has insufficient balance for the payment", BAD_REQUEST);
        }
        return account;
    }

    private Account getAccount(long number, String type) {
        try {
            return em.createNamedQuery("Account.findByNumber", Account.class)
                    .setParameter("number", number)
                    .setLockMode(PESSIMISTIC_WRITE)
                    .getSingleResult();
        } catch (NoResultException ex) {
            throw new ApplicationException("No " + type + "Account found", BAD_REQUEST);
        }
    }

}
