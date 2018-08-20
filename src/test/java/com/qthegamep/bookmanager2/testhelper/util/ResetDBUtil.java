package com.qthegamep.bookmanager2.testhelper.util;

import com.qthegamep.bookmanager2.util.SessionUtil;

import lombok.experimental.UtilityClass;
import lombok.val;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * This class is an utility helper class that is responsible for resetting database to empty state.
 */
@UtilityClass
public class ResetDBUtil {

    private final String RESET_DATABASE_SQL_QUERY = getResetDatabaseSqlQuery();

    /**
     * This method reset database to empty state using system resource that can switch by maven profile.
     * The sql query will be loaded only once.
     */
    public void resetDatabase() {
        val session = SessionUtil.openTransactionSession();

        session.createNativeQuery(RESET_DATABASE_SQL_QUERY).executeUpdate();

        SessionUtil.closeTransactionSession();
    }

    private String getResetDatabaseSqlQuery() {
        var resetDatabaseSqlQuery = "";

        try {
            val bytes = Files.readAllBytes(Paths.get(ClassLoader.getSystemResource("initDB.sql").toURI()));

            resetDatabaseSqlQuery = new String(bytes);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return resetDatabaseSqlQuery;
    }
}
