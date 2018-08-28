package com.qthegamep.bookmanager2.service;

import com.qthegamep.bookmanager2.dao.BookDAO;
import com.qthegamep.bookmanager2.dao.BookDAOImpl;
import com.qthegamep.bookmanager2.entity.Book;
import com.qthegamep.bookmanager2.testhelper.rule.Rules;

import lombok.val;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.junit.rules.ExternalResource;
import org.junit.rules.Stopwatch;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookServiceImplTest {

    @ClassRule
    public static ExternalResource summaryRule = Rules.SUMMARY_RULE;
    @ClassRule
    public static ExternalResource recreateSessionFactoryRule = Rules.RECREATE_SESSION_FACTORY_RULE;

    @Rule
    public Stopwatch stopwatchRule = Rules.STOPWATCH_RULE;
    @Rule
    public ExternalResource resetDatabaseRule = Rules.RESET_DATABASE_RULE;

    private BookService bookService;
    private BookService bookServiceWithMock;

    private BookDAO bookDAO;
    @Mock
    private BookDAO bookDAOMock;

    private Book firstBook;
    private Book secondBook;

    private List<Book> books;

    @Before
    public void setUp() {
        bookService = new BookServiceImpl();
        bookServiceWithMock = new BookServiceImpl();

        bookDAO = new BookDAOImpl();

        ((BookServiceImpl) bookService).setBookDAO(bookDAO);
        ((BookServiceImpl) bookServiceWithMock).setBookDAO(bookDAOMock);

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

        books = List.of(firstBook, secondBook);
    }

    @Test
    public void shouldCreateObjectWithNoArgsConstructor() {
        assertThat(bookService).isNotNull();
        assertThat(bookServiceWithMock).isNotNull();
    }

    @Test
    public void shouldImplementsBookServiceInterface() {
        assertThat(bookService).isInstanceOf(BookService.class);
        assertThat(bookServiceWithMock).isInstanceOf(BookService.class);
    }

    @Test
    public void shouldBeEmptyDatabaseBeforeEachTest() {
        val allBooks = bookDAO.getAll();

        assertThat(allBooks)
                .isNotNull()
                .isEmpty();
    }

    @Test
    public void shouldGetAndSetBookDAO() {
        val newBookDAO = new BookDAOImpl();

        ((BookServiceImpl) bookService).setBookDAO(newBookDAO);

        assertThat(((BookServiceImpl) bookService).getBookDAO())
                .isNotNull()
                .isEqualTo(newBookDAO);
    }

    @Test
    public void shouldAddBookCorrectly() {
        bookService.add(firstBook);

        val allBooks = bookDAO.getAll();

        assertThat(allBooks)
                .isNotEmpty()
                .contains(firstBook);
    }

    @Test
    public void shouldCallAddMethodCorrectly() {
        bookServiceWithMock.add(firstBook);

        verify(bookDAOMock, times(1)).add(firstBook);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldAddAllBooksCorrectly() {
        bookService.addAll(books);

        val allBooks = bookDAO.getAll();

        assertThat(allBooks)
                .isNotEmpty()
                .isEqualTo(books);
    }

    @Test
    public void shouldCallAddAllMethodCorrectly() {
        bookServiceWithMock.addAll(books);

        verify(bookDAOMock, times(1)).addAll(books);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldGetByIdBookCorrectly() {
        bookDAO.add(firstBook);

        val book = bookService.getById(1);

        assertThat(book).isEqualTo(firstBook);
    }

    @Test
    public void shouldCallGetByIdMethodCorrectly() {
        bookServiceWithMock.getById(1);

        verify(bookDAOMock, times(1)).getById(1);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldGetByNameBooksCorrectly() {
        bookDAO.addAll(books);

        val allBooks = bookService.getByName("test firstBook");

        assertThat(allBooks)
                .isNotEmpty()
                .contains(firstBook);
    }

    @Test
    public void shouldCallGetByNameMethodCorrectly() {
        bookServiceWithMock.getByName("test firstBook");

        verify(bookDAOMock, times(1)).getByName("test firstBook");

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldGetByAuthorBooksCorrectly() {
        bookDAO.addAll(books);

        val allBooks = bookService.getByAuthor("test firstAuthor");

        assertThat(allBooks)
                .isNotEmpty()
                .contains(firstBook);
    }

    @Test
    public void shouldCallGetByAuthorMethodCorrectly() {
        bookServiceWithMock.getByAuthor("test firstAuthor");

        verify(bookDAOMock, times(1)).getByAuthor("test firstAuthor");

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldGetByPrintYearBooksCorrectly() {
        bookDAO.addAll(books);

        val allBooks = bookService.getByPrintYear(2000);

        assertThat(allBooks)
                .isNotEmpty()
                .contains(firstBook);
    }

    @Test
    public void shouldCallGetByPrintYearMethodCorrectly() {
        bookServiceWithMock.getByPrintYear(2000);

        verify(bookDAOMock, times(1)).getByPrintYear(2000);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldGetByIsReadBooksCorrectly() {
        bookDAO.addAll(books);

        val allBooks = bookService.getByIsRead(false);

        assertThat(allBooks)
                .isNotEmpty()
                .contains(firstBook);
    }

    @Test
    public void shouldCallGetByIsReadMethodCorrectly() {
        bookServiceWithMock.getByIsRead(false);

        verify(bookDAOMock, times(1)).getByIsRead(false);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldGetAllBooksCorrectly() {
        bookDAO.addAll(books);

        val allBooks = bookService.getAll();

        assertThat(allBooks)
                .isNotEmpty()
                .isEqualTo(books);
    }

    @Test
    public void shouldCallGetAllMethodCorrectly() {
        bookServiceWithMock.getAll();

        verify(bookDAOMock, times(1)).getAll();

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldUpdateBookCorrectly() {
        bookDAO.add(firstBook);

        firstBook.setPrintYear(9999);

        bookService.update(firstBook);

        val allBooks = bookDAO.getAll();

        assertThat(allBooks)
                .isNotEmpty()
                .contains(firstBook);
    }

    @Test
    public void shouldCallUpdateMethodCorrectly() {
        bookServiceWithMock.update(firstBook);

        verify(bookDAOMock, times(1)).update(firstBook);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldUpdateAllBooksCorrectly() {
        bookDAO.addAll(books);

        firstBook.setPrintYear(9999);
        secondBook.setPrintYear(8888);

        bookService.updateAll(books);

        val allBooks = bookDAO.getAll();

        assertThat(allBooks)
                .isNotEmpty()
                .isEqualTo(books);
    }

    @Test
    public void shouldCallUpdateAllMethodCorrectly() {
        bookServiceWithMock.updateAll(books);

        verify(bookDAOMock, times(1)).updateAll(books);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldRemoveBookCorrectly() {
        bookDAO.add(firstBook);

        bookService.remove(firstBook);

        val allBooks = bookDAO.getAll();

        assertThat(allBooks).isEmpty();
    }

    @Test
    public void shouldCallRemoveMethodCorrectly() {
        bookServiceWithMock.remove(firstBook);

        verify(bookDAOMock, times(1)).remove(firstBook);

        verifyNoMoreInteractions(bookDAOMock);
    }

    @Test
    public void shouldRemoveAllBooksCorrectly() {
        bookDAO.addAll(books);

        bookService.removeAll(books);

        val allBooks = bookDAO.getAll();

        assertThat(allBooks).isEmpty();
    }

    @Test
    public void shouldCallRemoveAllMethodCorrectly() {
        bookServiceWithMock.removeAll(books);

        verify(bookDAOMock, times(1)).removeAll(books);

        verifyNoMoreInteractions(bookDAOMock);
    }
}
