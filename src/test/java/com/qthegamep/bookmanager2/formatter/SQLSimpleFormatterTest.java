package com.qthegamep.bookmanager2.formatter;

import com.qthegamep.bookmanager2.testhelper.rule.Rules;

import lombok.val;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;
import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;

import static org.assertj.core.api.Assertions.*;

public class SQLSimpleFormatterTest {

    @ClassRule
    public static ExternalResource summaryRule = Rules.SUMMARY_RULE;

    @Rule
    public Stopwatch stopwatchRule = Rules.STOPWATCH_RULE;

    private SQLSimpleFormatter sqlSimpleFormatter;

    @Before
    public void setUp() {
        sqlSimpleFormatter = new SQLSimpleFormatter();
    }

    @Test
    public void shouldCreateObjectWithNoArgsConstructor() {
        assertThat(sqlSimpleFormatter).isNotNull();
    }

    @Test
    public void shouldImplementsMessageFormattingStrategyInterface() {
        assertThat(sqlSimpleFormatter).isInstanceOf(MessageFormattingStrategy.class);
    }

    @Test
    public void shouldWorkFormattingSqlQueryCorrectly() {
        val sqlQuery = "/* insert com.qthegamep.bookmanager2.entity.Book */ " +
                "insert into BOOKS (AUTHOR, IS_READ, NAME, PRINT_YEAR) values ('lele', true, 'keke', 1010)";
        val elapsed = 973;

        val actualFormattedQuery = sqlSimpleFormatter.formatMessage(
                0,
                "",
                elapsed,
                "",
                "",
                sqlQuery
        );

        val expectedFormattedQuery = "P6Spy - Hibernate:  " + System.lineSeparator() +
                "    /* insert com.qthegamep.bookmanager2.entity.Book" + System.lineSeparator() +
                "        */ insert " + System.lineSeparator() +
                "        into" + System.lineSeparator() +
                "            BOOKS" + System.lineSeparator() +
                "            (AUTHOR, IS_READ, NAME, PRINT_YEAR) " + System.lineSeparator() +
                "        values" + System.lineSeparator() +
                "            ('lele', true, 'keke', 1010) {elapsed: 973ms}";

        assertThat(actualFormattedQuery).isEqualTo(expectedFormattedQuery);
    }

    @Test
    public void shouldWorkFormattingSqlQueryWithBatchCorrectly() {
        val sqlQuery = "/* insert com.qthegamep.bookmanager2.entity.Book */ " +
                "insert into BOOKS (AUTHOR, IS_READ, NAME, PRINT_YEAR) values ('lele', true, 'keke', 1010)";
        val elapsed = 973;
        val category = "batch";

        val actualFormattedQuery = sqlSimpleFormatter.formatMessage(
                0,
                "",
                elapsed,
                category,
                "",
                sqlQuery
        );

        val expectedFormattedQuery = "P6Spy - Hibernate:  add to batch  " + System.lineSeparator() +
                "    /* insert com.qthegamep.bookmanager2.entity.Book" + System.lineSeparator() +
                "        */ insert " + System.lineSeparator() +
                "        into" + System.lineSeparator() +
                "            BOOKS" + System.lineSeparator() +
                "            (AUTHOR, IS_READ, NAME, PRINT_YEAR) " + System.lineSeparator() +
                "        values" + System.lineSeparator() +
                "            ('lele', true, 'keke', 1010) {elapsed: 973ms}";

        assertThat(actualFormattedQuery).isEqualTo(expectedFormattedQuery);
    }

    @Test
    public void shouldReturnEmptyStringWhenSqlQueryIsEmpty() {
        val actualFormattedQuery = sqlSimpleFormatter.formatMessage(
                0,
                "",
                0,
                "",
                "",
                ""
        );

        assertThat(actualFormattedQuery).isEmpty();
    }
}
