package com.qthegamep.bookmanager2.formatter;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

/**
 * This class is a formatter class responsible for formatting hibernate logging sql queries using P6Spy proxy.
 */
@Slf4j
public class SQLSimpleFormatter implements MessageFormattingStrategy {

    private static final Formatter HIBERNATE_SQL_FORMATTER = new BasicFormatterImpl();

    /**
     * This method is response for formatting hibernate sql query.
     *
     * @param elapsed  is lead time.
     * @param category is category of sql operation.
     * @param sql      is query that has not been formatted yes.
     * @return a formatted request.
     */
    @Override
    public String formatMessage(int connectionId, String now, long elapsed,
                                String category, String prepared, String sql) {
        log.info("Preparing to formatting sql query: {}", sql);

        if (sql.isEmpty()) {
            log.info("\"Preparing to formatting sql query: {} was done successful! Query is empty", sql);
            return "";
        }

        val batch = "batch".equals(category) ? " add to batch " : "";

        return String.format("P6Spy - Hibernate: %s %s {elapsed: %dms}",
                batch,
                HIBERNATE_SQL_FORMATTER.format(sql),
                elapsed
        );
    }
}
