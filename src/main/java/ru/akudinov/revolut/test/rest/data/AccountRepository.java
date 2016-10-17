package ru.akudinov.revolut.test.rest.data;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.glassfish.hk2.extras.interception.Intercepted;
import ru.akudinov.revolut.test.rest.exceptions.AlreadyHasIdentifierException;
import ru.akudinov.revolut.test.rest.exceptions.ApplicationException;
import ru.akudinov.revolut.test.rest.exceptions.EntityIdentifierNotExistException;
import ru.akudinov.revolut.test.rest.exceptions.EntityNotFoundException;
import ru.akudinov.revolut.test.rest.model.Account;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.sql.SQLIntegrityConstraintViolationException;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static ru.akudinov.revolut.test.rest.data.TransactionalRunner.runInTransaction;

@Intercepted
public class AccountRepository {

    @Inject
    private EntityManager em;


    public Account create(Account account) {
        return runInTransaction(em, () -> {
                    if (account.getId() != null) {
                        throw new AlreadyHasIdentifierException();
                    }

                    try {
                        em.persist(account);
                        em.flush();
                        return account;
                    } catch (PersistenceException pe) {
                        if (pe.getCause() instanceof DatabaseException) {
                            DatabaseException de = (DatabaseException) pe.getCause();
                            if (de.getCause() instanceof SQLIntegrityConstraintViolationException) {
                                throw new ApplicationException("This account number is already exists", BAD_REQUEST);
                            }
                        }
                        throw pe;
                    }
                }
        );
    }

    public Account update(Account account) {
        return runInTransaction(em, () ->
        {
            em.merge(account);
            if (account.getId() == null) {
                throw new EntityIdentifierNotExistException();
            }
            return account;
        });
    }


    public Account read(long id) {
        return runInTransaction(em, () -> {
            Account account = em.find(Account.class, id);
            if (account == null) {
                throw new EntityNotFoundException();
            }

            return account;
        });
    }
}
