package com.qthegamep.bookmanager2.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

/**
 * This class is an utility helper class responsible for opening and closing sessions
 * and transactional sessions with the database.
 */
@Slf4j
@UtilityClass
public class SessionUtil {

    private final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private Session session;
    private Transaction transaction;

    /**
     * This method opens the session if session was not created or opened yet.
     *
     * @return session with the database.
     */
    public Session openSession() {
        log.info("Preparing to open hibernate session");

        if (session != null && session.isOpen()) {
            log.info("Preparing to open hibernate session was done successful! " +
                    "New session was not opened because it was already opened"
            );
            return session;
        }

        session = SESSION_FACTORY.openSession();
        log.info("Preparing to open hibernate session was done successful! New session was opened");

        return session;
    }

    /**
     * This method closes the session if session is created or opened.
     */
    public void closeSession() {
        log.info("Preparing to close hibernate session");

        if (session != null && session.isOpen()) {
            session.close();
            log.info("Preparing to close hibernate session was done successful! This session was closed");
        } else {
            log.info("Preparing to close hibernate session was done successful! " +
                    "This session was not close because it was already closed"
            );
        }
    }

    /**
     * This method opens the transactional session.
     *
     * @return transactional session with the database.
     */
    public Session openTransactionSession() {
        log.info("Preparing to open hibernate transaction session");

        openSession();

        log.info("Preparing to begin transaction");

        transaction = session.beginTransaction();
        log.info("Preparing to open hibernate transaction session was done successful! Transaction was started");

        return session;
    }

    /**
     * This method commit transaction if transaction was created and closes the session.
     */
    public void closeTransactionSession() {
        log.info("Preparing to close hibernate transaction session");

        if (transaction != null) {
            transaction.commit();
            log.info("Transaction was committed");
        } else {
            log.info("Transaction was not committed because it was not created yet");
        }

        closeSession();

        log.info("Preparing to close hibernate transaction session was done successful");
    }

    /**
     * This method closes the transactional session and session factory.
     * Use this method in the end of the application.
     */
    public void shutdown() {
        log.info("Preparing to shutdown hibernate session factory");

        closeTransactionSession();
        closeSessionFactory();

        log.info("Preparing to shutdown hibernate session factory was done successful");
    }

    private SessionFactory buildSessionFactory() {
        log.info("Preparing to build session factory");

        val sessionFactory = new Configuration().configure().buildSessionFactory();
        log.info("Preparing to build session factory was done successful");

        return sessionFactory;
    }

    private void closeSessionFactory() {
        log.info("Preparing to close session factory");

        SESSION_FACTORY.close();
        log.info("Preparing to close session factory was done successful! This session factory was closed");
    }
}
