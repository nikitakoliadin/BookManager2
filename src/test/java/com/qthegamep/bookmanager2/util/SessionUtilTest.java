package com.qthegamep.bookmanager2.util;

import com.qthegamep.bookmanager2.testhelper.rule.Rules;
import com.qthegamep.bookmanager2.testhelper.util.SessionFactoryUtil;

import lombok.val;
import org.junit.Before;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.hibernate.Session;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

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
    public void shouldBeNotNullSession() {
        assertThat(session).isNotNull();
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
    public void shouldBeNotNullTransactionSession() {
        session = SessionUtil.openTransactionSession();

        assertThat(session).isNotNull();

        SessionUtil.closeTransactionSession();
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
    public void shouldShutdownCorrectly() throws Exception {
        session = SessionUtil.openTransactionSession();

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        SessionFactoryUtil.createNewSessionFactory();
    }

    @Test
    public void shouldNotShutdownIfSessionFactoryIsNull() throws Exception {
        session = SessionUtil.openTransactionSession();

        val sessionFactoryField = SessionUtil.class.getDeclaredField("SESSION_FACTORY");
        sessionFactoryField.setAccessible(true);

        val modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(sessionFactoryField, sessionFactoryField.getModifiers() & ~Modifier.FINAL);

        sessionFactoryField.set(SessionUtil.class, null);

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        modifiers.setInt(sessionFactoryField, sessionFactoryField.getModifiers() | Modifier.FINAL);
        modifiers.setAccessible(false);

        sessionFactoryField.setAccessible(false);

        SessionFactoryUtil.createNewSessionFactory();
    }

    @Test
    public void shouldNotShutdownIfItIsShutdownAlready() throws Exception {
        session = SessionUtil.openTransactionSession();

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        SessionFactoryUtil.createNewSessionFactory();
    }

    @Test
    public void shouldThrowInvocationTargetExceptionWhenCreateObjectWithReflection() {
        assertThatExceptionOfType(InvocationTargetException.class).isThrownBy(() -> {
            val constructor = SessionUtil.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }).withMessage(null).withCauseInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void shouldThrowIllegalStateExceptionWhenStartOneMoreTransactionsAtTheSameTime() {
        session = SessionUtil.openTransactionSession();

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
                SessionUtil::openTransactionSession
        ).withMessage("Transaction already active");

        SessionUtil.closeTransactionSession();
    }
}
