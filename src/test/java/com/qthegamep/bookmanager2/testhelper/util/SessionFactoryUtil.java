package com.qthegamep.bookmanager2.testhelper.util;

import com.qthegamep.bookmanager2.util.SessionUtil;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * This class is an utility helper class that is responsible for creating new hibernate session factory
 * using reflection API.
 */
@UtilityClass
public class SessionFactoryUtil {

    /**
     * This method creates new hibernate session factory.
     *
     * @throws Exception when thrown no such field, method, invocation target or illegal access exception.
     */
    public void createNewSessionFactory() throws Exception {
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
