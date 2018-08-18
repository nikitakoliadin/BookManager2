package com.qthegamep.bookmanager2.util;

import com.qthegamep.bookmanager2.testhelper.rule.Rules;

import lombok.val;
import org.hibernate.Session;
import org.junit.*;
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

    @Test
    public void shouldBeNotNullSession() {
        session = SessionUtil.openSession();

        assertThat(session).isNotNull();

        SessionUtil.closeSession();
    }

    @Test
    public void shouldOpenSessionCorrectly() {
        session = SessionUtil.openSession();

        assertThat(session.isOpen()).isTrue();

        SessionUtil.closeSession();
    }

    @Test
    public void shouldBeTheSameSession() {
        session = SessionUtil.openSession();
        val session = SessionUtil.openSession();

        assertThat(session).isEqualTo(this.session);

        SessionUtil.closeSession();
    }

    @Test
    public void shouldCloseSessionCorrectly() {
        session = SessionUtil.openSession();
        SessionUtil.closeSession();

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldNotCloseSessionIfSessionIsNull() throws Exception {
        session = SessionUtil.openSession();

        setFieldToNull("session");

        SessionUtil.closeSession();
    }

    @Test
    public void shouldNotCloseSessionIfSessionIsClosedAlready() {
        session = SessionUtil.openSession();
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
        session = SessionUtil.openSession();
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

        setFieldToNull("transaction");

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

        setNewSessionFactory();
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

        setNewSessionFactory();
    }

    @Test
    public void shouldNotShutdownIfItIsShutdownAlready() throws Exception {
        session = SessionUtil.openTransactionSession();

        SessionUtil.shutdown();

        assertThat(session.isOpen()).isFalse();

        SessionUtil.shutdown();

        setNewSessionFactory();
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
    public void shouldThrowIllegalStateExceptionWhenStartOneMoreTransactions() {
        session = SessionUtil.openTransactionSession();

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(
                SessionUtil::openTransactionSession
        ).withMessage("Transaction already active");

        SessionUtil.closeTransactionSession();
    }

    private void setFieldToNull(String nameOfField) throws Exception {
        val field = SessionUtil.class.getDeclaredField(nameOfField);

        field.setAccessible(true);
        field.set(SessionUtil.class, null);

        assertThat(field.get(SessionUtil.class)).isNull();

        field.setAccessible(false);
    }

    private void setNewSessionFactory() throws Exception {
        val sessionFactoryField = SessionUtil.class.getDeclaredField("SESSION_FACTORY");
        sessionFactoryField.setAccessible(true);

        val modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(sessionFactoryField, sessionFactoryField.getModifiers() & ~Modifier.FINAL);

        val buildSessionFactoryMethod = SessionUtil.class.getDeclaredMethod("buildSessionFactory");
        buildSessionFactoryMethod.setAccessible(true);

        sessionFactoryField.set(SessionUtil.class, buildSessionFactoryMethod.invoke(SessionUtil.class));

        buildSessionFactoryMethod.setAccessible(false);

        modifiers.setInt(sessionFactoryField, sessionFactoryField.getModifiers() | Modifier.FINAL);
        modifiers.setAccessible(false);

        sessionFactoryField.setAccessible(false);
    }
}
