package com.qthegamep.bookmanager2.dao;

import com.qthegamep.bookmanager2.entity.Book;
import com.qthegamep.bookmanager2.util.SessionUtil;

import lombok.extern.slf4j.Slf4j;
import lombok.val;

import java.util.List;

/**
 * This class is DAO that implements all standard CRUD operations.
 */
@Slf4j
public class BookDAOImpl implements BookDAO {

    /**
     * This DAO method implements adding book entity object to the database.
     * This method is transactional.
     *
     * @param book is the entity object that will be added to the database.
     */
    @Override
    public void add(Book book) {
        log.info("Preparing to execute CREATE CRUD operation");

        val session = SessionUtil.openTransactionSession();

        try {
            log.info("Preparing to add entity! Entity to add: NAME = {}, AUTHOR = {}, PRINT_YEAR  = {}, IS_READ = {}",
                    book.getName(),
                    book.getAuthor(),
                    book.getPrintYear(),
                    book.isRead()
            );

            session.save(book);
            log.info("Preparing to add entity was done successful");

            SessionUtil.closeTransactionSession();
            log.info("Entity was added to the database");
        } catch (Exception e) {
            log.info("Preparing to rollback");

            session.getTransaction().rollback();
            log.info("Preparing to rollback was done successful! Exception message: [{}]",
                    e.getMessage(),
                    e
            );
        }

        log.info("Preparing to execute CREATE CRUD operation was done successful");
    }

    /**
     * This DAO method implements adding list of books entities objects to the database.
     * This method is transactional.
     *
     * @param books is the list of entities objects that will be added to the database.
     */
    @Override
    public void addAll(List<? extends Book> books) {
        log.info("Preparing to execute CREATE CRUD operation");

        val session = SessionUtil.openTransactionSession();

        try {
            log.info("Preparing to add list of entities! Entities to add: {}", books);

            books.forEach(session::save);
            log.info("Preparing to add list of entities was done successful");

            SessionUtil.closeTransactionSession();
            log.info("All entities was added to the database");
        } catch (Exception e) {
            log.info("Preparing to rollback");

            session.getTransaction().rollback();
            log.info("Preparing to rollback was done successful! Exception message: [{}]",
                    e.getMessage(),
                    e
            );
        }

        log.info("Preparing to execute CREATE CRUD operation was done successful");
    }

    /**
     * This DAO method implements returning book entity object from the database by id.
     * This method is transactional.
     *
     * @param id is the parameter by which the entity object will be returned.
     * @return book entity object.
     */
    @Override
    public Book getById(int id) {
        log.info("Preparing to execute READ CRUD operation");

        val session = SessionUtil.openTransactionSession();

        log.info("Preparing to get entity from the database by id = [{}]", id);

        val book = session.load(Book.class, id);
        log.info("Gotten entity: ID = {}, NAME = {}, AUTHOR = {}, PRINT_YEAR  = {}, IS_READ = {} - was gotten",
                book.getId(),
                book.getName(),
                book.getAuthor(),
                book.getPrintYear(),
                book.isRead()
        );

        SessionUtil.closeTransactionSession();
        log.info("Preparing to get entity from the database by id was done successful");

        log.info("Preparing to execute READ CRUD operation was done successful");

        return book;
    }

    /**
     * This DAO method implements returning list of books entities objects from the database by name.
     * This method is transactional.
     *
     * @param name is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    @Override
    public List<Book> getByName(String name) {
        log.info("Preparing to execute READ CRUD operation");

        val session = SessionUtil.openTransactionSession();

        log.info("Preparing to get entity from the database by name = [{}]", name);

        val books = session.createQuery("from Book where name = :name", Book.class)
                .setParameter("name", name)
                .list();
        log.info("Gotten entities: {}", books);

        SessionUtil.closeTransactionSession();
        log.info("Preparing to get entity from the database by name was done successful");

        log.info("Preparing to execute READ CRUD operation was done successful");

        return books;
    }

    /**
     * This DAO method implements returning list of books entities objects from the database by author.
     * This method is transactional.
     *
     * @param author is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    @Override
    public List<Book> getByAuthor(String author) {
        log.info("Preparing to execute READ CRUD operation");

        val session = SessionUtil.openTransactionSession();

        log.info("Preparing to get entity from the database by author = [{}]", author);

        val books = session.createQuery("from Book where author = :author", Book.class)
                .setParameter("author", author)
                .list();
        log.info("Gotten entities: {}", books);

        SessionUtil.closeTransactionSession();
        log.info("Preparing to get entity from the database by author was done successful");

        log.info("Preparing to execute READ CRUD operation was done successful");

        return books;
    }

    /**
     * This DAO method implements returning list of books entities objects from the database by print year.
     * This method is transactional.
     *
     * @param printYear is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    @Override
    public List<Book> getByPrintYear(int printYear) {
        log.info("Preparing to execute READ CRUD operation");

        val session = SessionUtil.openTransactionSession();

        log.info("Preparing to get entity from the database by printYear = [{}]", printYear);

        val books = session.createQuery("from Book where printYear = :printYear", Book.class)
                .setParameter("printYear", printYear)
                .list();
        log.info("Gotten entities: {}", books);

        SessionUtil.closeTransactionSession();
        log.info("Preparing to get entity from the database by printYear was done successful");

        log.info("Preparing to execute READ CRUD operation was done successful");

        return books;
    }

    /**
     * This DAO method implements returning list of books entities objects from the database by is read.
     * This method is transactional.
     *
     * @param isRead is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    @Override
    public List<Book> getByIsRead(boolean isRead) {
        log.info("Preparing to execute READ CRUD operation");

        val session = SessionUtil.openTransactionSession();

        log.info("Preparing to get entity from the database by isRead = [{}]", isRead);

        val books = session.createQuery("from Book where isRead = :isRead", Book.class)
                .setParameter("isRead", isRead)
                .list();
        log.info("Gotten entities: {}", books);

        SessionUtil.closeTransactionSession();
        log.info("Preparing to get entity from the database by isRead was done successful");

        log.info("Preparing to execute READ CRUD operation was done successful");

        return books;
    }

    /**
     * This DAO method implements returning list of all books entities objects from the database.
     * This method is transactional.
     *
     * @return list of books entities objects.
     */
    @Override
    public List<Book> getAll() {
        return null;
    }

    /**
     * This DAO method implements updating book entity object in the database.
     * This method is transactional.
     *
     * @param book is the new entity that will be added to the database instead of the old one.
     */
    @Override
    public void update(Book book) {

    }

    /**
     * This DAO method implements updating list of books entities objects in the database.
     * This method is transactional.
     *
     * @param books is the new entities that will be added to the database instead of the old ones.
     */
    @Override
    public void updateAll(List<? extends Book> books) {

    }

    /**
     * This DAO method implements deleting book entity object from the database.
     * This method is transactional.
     *
     * @param book is the entity that will be deleted from the database.
     */
    @Override
    public void remove(Book book) {

    }

    /**
     * This DAO method implements deleting list of books entities objects from the database.
     * This method is transactional.
     *
     * @param books is the entities that will be deleted from the database.
     */
    @Override
    public void removeAll(List<? extends Book> books) {

    }
}
