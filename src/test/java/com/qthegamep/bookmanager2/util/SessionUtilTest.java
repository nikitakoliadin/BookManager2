package com.qthegamep.bookmanager2.util;

import com.qthegamep.bookmanager2.testhelper.rule.Rules;

import lombok.val;
import org.junit.Before;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.hibernate.Session;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;

import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.*;

public class SessionUtilTest {

    @ClassRule
    public static ExternalResource summaryRule = Rules.SUMMARY_RULE;

    @Rule
    public Stopwatch stopwatchRule = Rules.STOPWATCH_RULE;

    private Session session;

    @Before
    public void setUp() {
        session = SessionUtil.openSession();
    }

    @After
    public void tearDown() {
        SessionUtil.closeSession();
    }

    @Test
    public void shouldBeNotNullSessionFactory() {
        assertThat(session.getSessionFactory()).isNotNull();
    }

    @Test
    public void shouldBeNotNullSession() {
        assertThat(session).isNotNull();
    }

    @Test
    public void shouldBeNotNullTransactionSession() {
        session = SessionUtil.openTransactionSession();

        assertThat(session).isNotNull();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldCreateNewSessionFactoryIfOldSessionFactoryIsNull() throws Exception {
        val oldSessionFactory = session.getSessionFactory();

        SessionUtil.closeSession();

        val sessionFactoryField = SessionUtil.class.getDeclaredField("sessionFactory");

        sessionFactoryField.setAccessible(true);
        sessionFactoryField.set(SessionUtil.class, null);

        SessionUtil.createNewSessionFactory();

        val newSessionFactory = SessionUtil.openSession().getSessionFactory();

        assertThat(oldSessionFactory).isNotEqualTo(newSessionFactory);

        sessionFactoryField.setAccessible(false);
    }

    @Test
    public void shouldCreateNewSessionFactoryIfOldSessionFactoryIsClosed() {
        val oldSessionFactory = session.getSessionFactory();

        SessionUtil.shutdown();

        SessionUtil.createNewSessionFactory();

        session = SessionUtil.openSession();

        val newSessionFactory = session.getSessionFactory();

        assertThat(oldSessionFactory).isNotEqualTo(newSessionFactory);
    }

    @Test
    public void shouldNotCreateNewSessionFactoryIfOldSessionFactoryIsOpened() {
        val oldSessionFactory = session.getSessionFactory();

        SessionUtil.createNewSessionFactory();

        session = SessionUtil.openSession();

        val newSessionFactory = session.getSessionFactory();

        assertThat(oldSessionFactory).isEqualTo(newSessionFactory);
    }

    @Test
    public void shouldOpenSessionCorrectly() {
        assertThat(session.isOpen()).isTrue();
    }

    @Test
    public void shouldBeTheSameSession() {
        val session = SessionUtil.openSession();

        assertThat(session).isEqualTo(this.session);
    }

    @Test
    public void shouldCloseSessionCorrectly() {
        SessionUtil.closeSession();

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldNotCloseSessionIfSessionIsNull() throws Exception {
        val field = SessionUtil.class.getDeclaredField("session");

        field.setAccessible(true);
        field.set(SessionUtil.class, null);

        assertThat(field.get(SessionUtil.class)).isNull();

        field.setAccessible(false);
    }

    @Test
    public void shouldNotCloseSessionIfSessionIsClosedAlready() {
        SessionUtil.closeSession();

        assertThat(session.isOpen()).isFalse();

        SessionUtil.closeSession();

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldOpenTransactionSessionCorrectly() {
        session = SessionUtil.openTransactionSession();

        assertThat(session.isOpen()).isTrue();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldBeTheSameTransactionSessionAndSession() {
        val transactionSession = SessionUtil.openTransactionSession();

        assertThat(transactionSession).isEqualTo(session);

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldStartTransaction() {
        session = SessionUtil.openTransactionSession();

        val transaction = session.getTransaction();

        assertThat(transaction.isActive()).isTrue();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldCloseTransactionSessionCorrectly() {
        session = SessionUtil.openTransactionSession();

        SessionUtil.closeTransactionSession();

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldNotCommitIfTransactionIsNull() throws Exception {
        session = SessionUtil.openTransactionSession();

        val field = SessionUtil.class.getDeclaredField("transaction");

        field.setAccessible(true);
        field.set(SessionUtil.class, null);

        assertThat(field.get(SessionUtil.class)).isNull();

        field.setAccessible(false);

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldNotCommitIfTransactionIsNotActive() {
        session = SessionUtil.openTransactionSession();

        val transaction = session.getTransaction();

        transaction.rollback();

        assertThat(transaction.isActive()).isFalse();

        SessionUtil.closeTransactionSession();
    }

    @Test
    public void shouldShutdownCorrectly() {
        session = SessionUtil.openTransactionSession();

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        SessionUtil.createNewSessionFactory();
    }

    @Test
    public void shouldNotShutdownIfSessionFactoryIsNull() throws Exception {
        session = SessionUtil.openTransactionSession();

        val sessionFactoryField = SessionUtil.class.getDeclaredField("sessionFactory");

        sessionFactoryField.setAccessible(true);
        sessionFactoryField.set(SessionUtil.class, null);

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        sessionFactoryField.setAccessible(false);

        SessionUtil.createNewSessionFactory();
    }

    @Test
    public void shouldNotShutdownIfItIsShutdownAlready() {
        session = SessionUtil.openTransactionSession();

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

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
        session = SessionUtil.openTransactionSession();

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
