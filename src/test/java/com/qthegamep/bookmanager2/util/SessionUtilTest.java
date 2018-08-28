package com.qthegamep.bookmanager2.util;

import com.qthegamep.bookmanager2.testhelper.rule.Rules;

import lombok.val;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.*;

public class SessionUtilTest {

    @ClassRule
    public static ExternalResource summaryRule = Rules.SUMMARY_RULE;
    @ClassRule
    public static ExternalResource recreateSessionFactoryRule = Rules.RECREATE_SESSION_FACTORY_RULE;

    @Rule
    public Stopwatch stopwatchRule = Rules.STOPWATCH_RULE;

    @Test
    public void shouldBeNotNullSessionFactory() {
        val session = SessionUtil.openSession();

        assertThat(session.getSessionFactory()).isNotNull();

        SessionUtil.closeSession();
    }

    @Test
    public void shouldBeNotNullSession() {
        val session = SessionUtil.openSession();

        assertThat(session).isNotNull();

        SessionUtil.closeSession();
    }

    @Test
    public void shouldBeNotNullTransactionSession() {
        val transactionSession = SessionUtil.openTransactionSession();

        assertThat(transactionSession).isNotNull();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldCreateNewSessionFactoryIfOldSessionFactoryIsNull() throws Exception {
        val session = SessionUtil.openSession();

        val oldSessionFactory = session.getSessionFactory();

        SessionUtil.closeSession();

        val sessionFactoryField = SessionUtil.class.getDeclaredField("sessionFactory");

        sessionFactoryField.setAccessible(true);
        val oldSessionFactoryField = sessionFactoryField.get(SessionUtil.class);
        sessionFactoryField.set(SessionUtil.class, null);

        SessionUtil.createNewSessionFactory();

        val newSessionFactory = SessionUtil.openSession().getSessionFactory();

        assertThat(oldSessionFactory).isNotEqualTo(newSessionFactory);

        SessionUtil.shutdown();

        sessionFactoryField.set(SessionUtil.class, oldSessionFactoryField);
        sessionFactoryField.setAccessible(false);

        SessionUtil.closeSession();
    }

    @Test
    public void shouldCreateNewSessionFactoryIfOldSessionFactoryIsClosed() {
        var session = SessionUtil.openSession();

        val oldSessionFactory = session.getSessionFactory();

        SessionUtil.shutdown();

        SessionUtil.createNewSessionFactory();

        session = SessionUtil.openSession();

        val newSessionFactory = session.getSessionFactory();

        assertThat(oldSessionFactory).isNotEqualTo(newSessionFactory);

        SessionUtil.closeSession();
    }

    @Test
    public void shouldNotCreateNewSessionFactoryIfOldSessionFactoryIsOpened() {
        var session = SessionUtil.openSession();

        val oldSessionFactory = session.getSessionFactory();

        SessionUtil.createNewSessionFactory();

        session = SessionUtil.openSession();

        val newSessionFactory = session.getSessionFactory();

        assertThat(oldSessionFactory).isEqualTo(newSessionFactory);

        SessionUtil.closeSession();
    }

    @Test
    public void shouldOpenSessionCorrectly() {
        val session = SessionUtil.openSession();

        assertThat(session.isOpen()).isTrue();

        SessionUtil.closeSession();
    }

    @Test
    public void shouldBeTheSameSession() {
        val session = SessionUtil.openSession();
        val newSession = SessionUtil.openSession();

        assertThat(session).isEqualTo(newSession);

        SessionUtil.closeSession();
    }

    @Test
    public void shouldCloseSessionCorrectly() {
        val session = SessionUtil.openSession();

        SessionUtil.closeSession();

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldNotCloseSessionIfSessionIsNull() throws Exception {
        SessionUtil.openSession();

        val sessionField = SessionUtil.class.getDeclaredField("session");

        sessionField.setAccessible(true);
        val oldSessionField = sessionField.get(SessionUtil.class);
        sessionField.set(SessionUtil.class, null);

        SessionUtil.closeSession();

        assertThat(sessionField.get(SessionUtil.class)).isNull();

        sessionField.set(SessionUtil.class, oldSessionField);
        sessionField.setAccessible(false);

        SessionUtil.closeSession();
    }

    @Test
    public void shouldNotCloseSessionIfSessionIsClosedAlready() {
        val session = SessionUtil.openSession();

        SessionUtil.closeSession();

        assertThat(session.isOpen()).isFalse();

        SessionUtil.closeSession();
    }

    @Test
    public void shouldOpenTransactionSessionCorrectly() {
        val transactionSession = SessionUtil.openTransactionSession();

        assertThat(transactionSession.isOpen()).isTrue();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldBeTheSameTransactionSessionAndSession() {
        val session = SessionUtil.openSession();
        val transactionSession = SessionUtil.openTransactionSession();

        assertThat(transactionSession).isEqualTo(session);

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldStartTransaction() {
        val transactionSession = SessionUtil.openTransactionSession();

        val transaction = transactionSession.getTransaction();

        assertThat(transaction.isActive()).isTrue();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldCloseTransactionSessionCorrectly() {
        val transactionSession = SessionUtil.openTransactionSession();

        SessionUtil.closeTransactionSession();

        assertThat(transactionSession.isOpen()).isFalse();
    }

    @Test
    public void shouldNotCommitIfTransactionIsNull() throws Exception {
        SessionUtil.openTransactionSession();

        val transactionField = SessionUtil.class.getDeclaredField("transaction");

        transactionField.setAccessible(true);
        transactionField.set(SessionUtil.class, null);

        assertThat(transactionField.get(SessionUtil.class)).isNull();

        transactionField.setAccessible(false);

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldNotCommitIfTransactionIsNotActive() {
        val transactionSession = SessionUtil.openTransactionSession();

        val transaction = transactionSession.getTransaction();

        transaction.rollback();

        assertThat(transaction.isActive()).isFalse();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldShutdownCorrectly() {
        val transactionSession = SessionUtil.openTransactionSession();

        SessionUtil.shutdown();

        assertThat(transactionSession.isOpen()).isFalse();

        SessionUtil.createNewSessionFactory();
    }

    @Test
    public void shouldNotShutdownIfSessionFactoryIsNull() throws Exception {
        val transactionSession = SessionUtil.openTransactionSession();

        val sessionFactoryField = SessionUtil.class.getDeclaredField("sessionFactory");

        sessionFactoryField.setAccessible(true);
        val oldSessionFactoryFieldValue = sessionFactoryField.get(SessionUtil.class);
        sessionFactoryField.set(SessionUtil.class, null);

        SessionUtil.shutdown();

        assertThat(transactionSession.isOpen()).isFalse();

        sessionFactoryField.set(SessionUtil.class, oldSessionFactoryFieldValue);
        sessionFactoryField.setAccessible(false);

        SessionUtil.createNewSessionFactory();
    }

    @Test
    public void shouldNotShutdownIfItIsShutdownAlready() {
        val transactionSession = SessionUtil.openTransactionSession();

        SessionUtil.shutdown();

        assertThat(transactionSession.isOpen()).isFalse();

        SessionUtil.shutdown();

        assertThat(transactionSession.isOpen()).isFalse();

        SessionUtil.createNewSessionFactory();
    }

    @Test
    public void shouldThrowInvocationTargetExceptionWhenCreateObjectWithReflection() {
        assertThatExceptionOfType(InvocationTargetException.class)
                .isThrownBy(
                        () -> {
                            val constructor = SessionUtil.class.getDeclaredConstructor();
                            constructor.setAccessible(true);
                            constructor.newInstance();
                        })
                .withMessage(null)
                .withCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenStartOneMoreTransactionsAtTheSameTime() {
        SessionUtil.openTransactionSession();

        assertThatIllegalStateException()
                .isThrownBy(SessionUtil::openTransactionSession)
                .withMessage("Transaction already active");

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenOpenSessionIfSessionFactoryIsClosed() {
        SessionUtil.shutdown();

        assertThatIllegalStateException()
                .isThrownBy(SessionUtil::openSession)
                .withMessage("EntityManagerFactory is closed");

        SessionUtil.createNewSessionFactory();
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenOpenTransactionSessionIfSessionFactoryIsClosed() {
        SessionUtil.shutdown();

        assertThatIllegalStateException()
                .isThrownBy(SessionUtil::openTransactionSession)
                .withMessage("EntityManagerFactory is closed");

        SessionUtil.createNewSessionFactory();
    }
}
