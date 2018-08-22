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
    public void shouldBeCloseSessionAfterRollbackAddMethod() {
        firstBook.setName(null);

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

        val thirdBook = new Book();

        thirdBook.setId(3);
        thirdBook.setName(firstBook.getName());
        thirdBook.setAuthor(firstBook.getAuthor());
        thirdBook.setPrintYear(firstBook.getPrintYear());
        thirdBook.setRead(firstBook.isRead());

        val fourthBook = new Book();

        fourthBook.setId(4);
        fourthBook.setName(secondBook.getName());
        fourthBook.setAuthor(secondBook.getAuthor());
        fourthBook.setPrintYear(secondBook.getPrintYear());
        fourthBook.setRead(secondBook.isRead());

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(4).contains(firstBook, secondBook, thirdBook, fourthBook);
    }

    @Test
    public void shouldRollbackAddAllMethodWhenInputParameterIsIncorrect() {
        secondBook.setName(null);

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
    public void shouldBeCloseSessionAfterRollbackAddAllMethod() {
        secondBook.setName(null);

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
    public void shouldBeCloseSessionAfterGetByIdMethodIfIdIsNotExist() {
        assertThatExceptionOfType(ObjectNotFoundException.class).isThrownBy(
                () -> bookDAO.getById(1)
        ).withMessage("No row with the given identifier exists: [com.qthegamep.bookmanager2.entity.Book#1]");

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

    @Test
    public void shouldGetAllEntitiesFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        var allEntitiesFromTheDatabase = bookDAO.getAll();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);

        addAllEntitiesToTheDatabase(books);

        allEntitiesFromTheDatabase = bookDAO.getAll();

        val thirdBook = new Book();

        thirdBook.setId(3);
        thirdBook.setName(firstBook.getName());
        thirdBook.setAuthor(firstBook.getAuthor());
        thirdBook.setPrintYear(firstBook.getPrintYear());
        thirdBook.setRead(firstBook.isRead());

        val fourthBook = new Book();

        fourthBook.setId(4);
        fourthBook.setName(secondBook.getName());
        fourthBook.setAuthor(secondBook.getAuthor());
        fourthBook.setPrintYear(secondBook.getPrintYear());
        fourthBook.setRead(secondBook.isRead());

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(4).contains(firstBook, secondBook, thirdBook, fourthBook);
    }

    @Test
    public void shouldGetAllMethodReturnEmptyEntitiesListCorrectly() {
        val books = bookDAO.getAll();

        assertThat(books).isNotNull().isEmpty();
    }

    @Test
    public void shouldBeCloseSessionAfterGetAllMethod() {
        bookDAO.getAll();

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldUpdateEntityInTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        firstBook.setName("shouldBeUpdated");
        firstBook.setAuthor("shouldBeUpdated");
        firstBook.setPrintYear(1111);
        firstBook.setRead(true);

        bookDAO.update(firstBook);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);

        secondBook.setName("shouldBeUpdated");
        secondBook.setAuthor("shouldBeUpdated");
        secondBook.setPrintYear(1111);
        secondBook.setRead(false);

        bookDAO.update(secondBook);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);
    }

    @Test
    public void shouldRollbackUpdateMethodWhenInputParameterIsIncorrect() {
        addAllEntitiesToTheDatabase(books);

        firstBook.setName("shouldBeUpdated");
        firstBook.setAuthor("shouldBeUpdated");
        firstBook.setPrintYear(1111);
        firstBook.setRead(true);

        bookDAO.update(firstBook);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);

        val updatedSecondBook = new Book();

        updatedSecondBook.setId(secondBook.getId());
        updatedSecondBook.setName(null);
        updatedSecondBook.setAuthor(secondBook.getAuthor());
        updatedSecondBook.setPrintYear(secondBook.getPrintYear());
        updatedSecondBook.setRead(secondBook.isRead());

        bookDAO.update(updatedSecondBook);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook).doesNotContain(updatedSecondBook);
    }

    @Test
    public void shouldBeCloseSessionAfterUpdateMethod() {
        bookDAO.update(firstBook);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldBeCloseSessionAfterRollbackUpdateMethod() {
        firstBook.setName(null);

        bookDAO.update(firstBook);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldUpdateAllEntitiesInTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        firstBook.setName("shouldBeUpdated");
        firstBook.setAuthor("shouldBeUpdated");
        firstBook.setPrintYear(1111);
        firstBook.setRead(true);

        secondBook.setName("shouldBeUpdated");
        secondBook.setAuthor("shouldBeUpdated");
        secondBook.setPrintYear(1111);
        secondBook.setRead(false);

        bookDAO.updateAll(books);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);

        firstBook.setRead(false);

        secondBook.setRead(true);

        bookDAO.updateAll(books);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);
    }

    @Test
    public void shouldRollbackUpdateAllMethodWhenInputParameterIsIncorrect() {
        addAllEntitiesToTheDatabase(books);

        firstBook.setName("shouldBeUpdated");
        firstBook.setAuthor("shouldBeUpdated");
        firstBook.setPrintYear(1111);
        firstBook.setRead(true);

        secondBook.setName("shouldBeUpdated");
        secondBook.setAuthor("shouldBeUpdated");
        secondBook.setPrintYear(1111);
        secondBook.setRead(false);

        bookDAO.updateAll(books);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);

        val updatedSecondBook = new Book();

        updatedSecondBook.setId(secondBook.getId());
        updatedSecondBook.setName(null);
        updatedSecondBook.setAuthor(secondBook.getAuthor());
        updatedSecondBook.setPrintYear(secondBook.getPrintYear());
        updatedSecondBook.setRead(secondBook.isRead());

        bookDAO.updateAll(List.of(updatedSecondBook));

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook).doesNotContain(updatedSecondBook);
    }

    @Test
    public void shouldBeCloseSessionAfterUpdateAllMethod() {
        bookDAO.updateAll(books);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldBeCloseSessionAfterRollbackUpdateAllMethod() {
        secondBook.setName(null);

        bookDAO.updateAll(books);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldRemoveEntityFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        bookDAO.remove(firstBook);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(1).contains(secondBook);

        bookDAO.remove(secondBook);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().isEmpty();
    }

    @Test
    public void shouldRollbackRemoveMethodWhenInputParameterIsIncorrect() {
        addAllEntitiesToTheDatabase(books);

        bookDAO.remove(firstBook);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(1).contains(secondBook);

        val updatedSecondBook = new Book();

        updatedSecondBook.setId(secondBook.getId());
        updatedSecondBook.setName(null);
        updatedSecondBook.setAuthor(secondBook.getAuthor());
        updatedSecondBook.setPrintYear(secondBook.getPrintYear());
        updatedSecondBook.setRead(secondBook.isRead());

        bookDAO.remove(updatedSecondBook);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(1).contains(secondBook).doesNotContain(updatedSecondBook);
    }

    @Test
    public void shouldBeCloseSessionAfterRemoveMethod() {
        bookDAO.remove(firstBook);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldBeCloseSessionAfterRollbackRemoveMethod() {
        firstBook.setName(null);

        bookDAO.remove(firstBook);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldRemoveAllEntitiesFromTheDatabaseCorrectly() {
        addAllEntitiesToTheDatabase(books);

        bookDAO.removeAll(books);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().isEmpty();

        firstBook.setId(3);
        secondBook.setId(4);

        addAllEntitiesToTheDatabase(books);

        bookDAO.removeAll(books);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().isEmpty();
    }

    @Test
    public void shouldRollbackRemoveAllMethodWhenInputParameterIsIncorrect() {
        addAllEntitiesToTheDatabase(books);

        bookDAO.removeAll(books);

        var allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().isEmpty();

        addAllEntitiesToTheDatabase(books);

        books.add(null);

        firstBook.setId(3);
        secondBook.setId(4);

        bookDAO.removeAll(books);

        allEntitiesFromTheDatabase = getAllEntitiesFromTheDatabase();

        assertThat(allEntitiesFromTheDatabase).isNotNull().hasSize(2).contains(firstBook, secondBook);
    }

    @Test
    public void shouldBeCloseSessionAfterRemoveAllMethod() {
        bookDAO.removeAll(books);

        assertThat(session.isOpen()).isFalse();
    }

    @Test
    public void shouldBeCloseSessionAfterRollbackRemoveAllMethod() {
        secondBook.setName(null);

        bookDAO.remove(secondBook);

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
