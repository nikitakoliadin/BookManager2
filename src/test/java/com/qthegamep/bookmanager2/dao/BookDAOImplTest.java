package com.qthegamep.bookmanager2.dao;

import com.qthegamep.bookmanager2.entity.Book;
import com.qthegamep.bookmanager2.testhelper.rule.Rules;
import com.qthegamep.bookmanager2.util.SessionUtil;

import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.After;

import org.hibernate.Session;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class BookDAOImplTest {

    @ClassRule
    public static ExternalResource summaryRule = Rules.SUMMARY_RULE;

    @Rule
    public Stopwatch stopwatchRule = Rules.STOPWATCH_RULE;
    @Rule
    public ExternalResource resetDatabaseRule = Rules.RESET_DATABASE_RULE;

    private Session session;

    private BookDAO bookDAO;

    private Book firstBook;
    private Book secondBook;

    private List<Book> books;

    @Before
    public void setUp() {
        session = SessionUtil.openSession();

        bookDAO = new BookDAOImpl();

        firstBook = new Book();

        firstBook.setId(1);
        firstBook.setName("test firstBook");
        firstBook.setAuthor("test firstAuthor");
        firstBook.setPrintYear(2000);
        firstBook.setRead(false);

        secondBook = new Book();

        secondBook.setId(2);
        secondBook.setName("test secondBook");
        secondBook.setAuthor("test secondAuthor");
        secondBook.setPrintYear(2010);
        secondBook.setRead(true);

        books = new ArrayList<>(Arrays.asList(firstBook, secondBook));
    }

    @After
    public void tearDown() {
        SessionUtil.closeSession();
    }

    @Test
    public void shouldCreateObjectWithNoArgsConstructor() {
        assertThat(bookDAO).isNotNull();
    }

    @Test
    public void shouldImplementsBookDAOInterface() {
        assertThat(bookDAO).isInstanceOf(BookDAO.class);
    }

    @Test
    public void shouldBeEmptyDatabaseBeforeEachTest() {
        val allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isEmpty();
    }

    @Test
    public void shouldAddEntityToTheDatabaseCorrectly() {
        bookDAO.add(firstBook);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(1).contains(firstBook);

        bookDAO.add(secondBook);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);
    }

    @Test
    public void shouldRollbackAddMethodWhenEntityIsIncorrect() {
        secondBook.setName(null);

        bookDAO.add(secondBook);

        val allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().isEmpty();
    }

    @Test
    public void shouldBeCloseSessionAfterAddMethod() {
        bookDAO.add(firstBook);

        assertThat(session.isOpen()).isFalse();
    }

    private List<Book> getAllEntitiesFromTheDatabase() {
        session = SessionUtil.openTransactionSession();

        val bookList = session.createQuery("from Book", Book.class).list();

        SessionUtil.closeTransactionSession();

        return bookList;
    }

    private void addAllEntitiesToTheDatabase(@NotNull List<? extends Book> books) {
        session = SessionUtil.openTransactionSession();

        books.forEach(session::save);

        SessionUtil.closeTransactionSession();
    }
}
