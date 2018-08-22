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
import org.hibernate.ObjectNotFoundException;

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
    public void shouldRollbackAddMethodWhenInputParameterIsIncorrect() {
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

    @Test
    public void shouldAddAllEntitiesToTheDatabaseCorrectly() {
        bookDAO.addAll(books);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);

        bookDAO.addAll(books);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        val thirdBook = firstBook;
        val fourthBook = secondBook;

        thirdBook.setId(3);
        fourthBook.setId(4);

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(4).contains(firstBook, secondBook, thirdBook, fourthBook);
    }

    @Test
    public void shouldRollbackAddAllMethodWhenInputParameterIsIncorrect() {
        books.get(1).setName(null);

        bookDAO.addAll(books);

        val allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().isEmpty();
    }

    @Test
    public void shouldBeCloseSessionAfterAddAllMethod() {
        bookDAO.addAll(books);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldGetByIdEntityFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        val firstBook = bookDAO.getById(1);

        assertThat(firstBook).isEqualTo(this.firstBook);

        val secondBook = bookDAO.getById(2);

        assertThat(secondBook).isEqualTo(this.secondBook);
    }

    @Test
    public void shouldThrowObjectNotFoundExceptionWhenIdIsNotExist() {
        assertThatExceptionOfType(ObjectNotFoundException.class).isThrownBy(
                () -> bookDAO.getById(1)
        ).withMessage("No row with the given identifier exists: [com.qthegamep.bookmanager2.entity.Book#1]");
    }

    @Test
    public void shouldBeCloseSessionAfterGetByIdMethod() {
        addAllEntitiesToTheDatabase(books);

        bookDAO.getById(1);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldGetByNameEntitiesFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        var bookListByName = bookDAO.getByName("test firstBook");

        assertThat(bookListByName).isNotNull().hasSize(1).contains(firstBook);

        addAllEntitiesToTheDatabase(books);

        bookListByName = bookDAO.getByName("test secondBook");

        assertThat(bookListByName).isNotNull().hasSize(2).contains(secondBook);
    }

    @Test
    public void shouldGetByNameMethodReturnEmptyEntitiesListCorrectly() {
        var books = bookDAO.getByName("test firstBook");

        assertThat(books).isNotNull().isEmpty();
    }

    @Test
    public void shouldBeCloseSessionAfterGetByNameMethod() {
        bookDAO.getByName("test firstBook");

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldGetByAuthorEntitiesFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        var bookListByAuthor = bookDAO.getByAuthor("test firstAuthor");

        assertThat(bookListByAuthor).isNotNull().hasSize(1).contains(firstBook);

        addAllEntitiesToTheDatabase(books);

        bookListByAuthor = bookDAO.getByAuthor("test secondAuthor");

        assertThat(bookListByAuthor).isNotNull().hasSize(2).contains(secondBook);
    }

    @Test
    public void shouldGetByAuthorMethodReturnEmptyEntitiesListCorrectly() {
        val books = bookDAO.getByAuthor("test firstAuthor");

        assertThat(books).isNotNull().isEmpty();
    }

    @Test
    public void shouldBeCloseSessionAfterGetByAuthorMethod() {
        bookDAO.getByAuthor("test firstAuthor");

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldGetByPrintYearEntitiesFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        var bookListByPrintYear = bookDAO.getByPrintYear(2000);

        assertThat(bookListByPrintYear).isNotNull().hasSize(1).contains(firstBook);

        addAllEntitiesToTheDatabase(books);

        bookListByPrintYear = bookDAO.getByPrintYear(2010);

        assertThat(bookListByPrintYear).isNotNull().hasSize(2).contains(secondBook);
    }

    @Test
    public void shouldGetByPrintYearMethodReturnEmptyEntitiesListCorrectly() {
        val books = bookDAO.getByPrintYear(2000);

        assertThat(books).isNotNull().isEmpty();
    }

    @Test
    public void shouldBeCloseSessionAfterGetByPrintYearMethod() {
        bookDAO.getByPrintYear(2000);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldGetByIsReadEntitiesFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        var bookListByIsRead = bookDAO.getByIsRead(false);

        assertThat(bookListByIsRead).isNotNull().hasSize(1).contains(firstBook);

        addAllEntitiesToTheDatabase(books);

        bookListByIsRead = bookDAO.getByIsRead(true);

        assertThat(bookListByIsRead).isNotNull().hasSize(2).contains(secondBook);
    }

    @Test
    public void shouldGetByIsReadMethodReturnEmptyEntitiesListCorrectly() {
        val books = bookDAO.getByIsRead(false);

        assertThat(books).isNotNull().isEmpty();
    }

    @Test
    public void shouldBeCloseSessionAfterGetByIsReadMethod() {
        bookDAO.getByIsRead(false);

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
