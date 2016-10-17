package ru.akudinov.revolut.test.rest.data;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

public class TransactionalRunner {

    public final static <T> T runInTransaction(EntityManager em, TransactionalCall<T> r) {
        EntityTransaction tx = em.getTransaction();
        T result;
        boolean success = false;
        try {
            tx.begin();

            result = r.call();

            success = true;

            return result;
        } finally {
            if (tx.isActive()) {
                if (success && !tx.getRollbackOnly()) {
                    tx.commit();
                } else {
                    try {
                        tx.rollback();
                    } catch (Throwable ignored) {
                    }
                }
            }
        }
    }

    interface TransactionalCall<T> {
        T call();
    }
}
