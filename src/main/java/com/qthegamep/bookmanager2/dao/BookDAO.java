package com.qthegamep.bookmanager2.dao;

import com.qthegamep.bookmanager2.entity.Book;

import java.util.List;

/**
 * This interface is a DAO. It has all the standard CRUD operations.
 */
public interface BookDAO {

    /**
     * This DAO method should add book entity object to the database.
     * This method should be transactional.
     *
     * @param book is the entity object that will be added to the database.
     */
    void add(Book book);

    /**
     * This DAO method should add list of books entities objects to the database.
     * This method should be transactional.
     * This method should uses a batch for multiple queries.
     *
     * @param books is the list of entities objects that will be added to the database.
     */
    void addAll(List<? extends Book> books);

    /**
     * This DAO method should return book entity object from the database by id.
     * This method should be transactional.
     *
     * @param id is the parameter by which the entity object will be returned.
     * @return book entity object.
     */
    Book getById(int id);

    /**
     * This DAO method should return list of books entities objects from the database by name.
     * This method should be transactional.
     *
     * @param name is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    List<Book> getByName(String name);

    /**
     * This DAO method should return list of books entities objects from the database by author.
     * This method should be transactional.
     *
     * @param author is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    List<Book> getByAuthor(String author);

    /**
     * This DAO method should return list of books entities objects from the database by print year.
     * This method should be transactional.
     *
     * @param printYear is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    List<Book> getByPrintYear(int printYear);

    /**
     * This DAO method should return list of books entities objects from the database by is read.
     * This method should be transactional.
     *
     * @param isRead is the parameter by which the list of entities objects will be returned.
     * @return list of books entities objects.
     */
    List<Book> getByIsRead(boolean isRead);

    /**
     * This DAO method should return list of all books entities objects from the database.
     * This method should be transactional.
     *
     * @return list of books entities objects.
     */
    List<Book> getAll();

    /**
     * This DAO method should update book entity object in the database.
     * This method should be transactional.
     *
     * @param book is the new entity that will be added to the database instead of the old one.
     */
    void update(Book book);

    /**
     * This DAO method should update list of books entities objects in the database.
     * This method should be transactional.
     * This method should uses a batch for multiple queries.
     *
     * @param books is the new entities that will be added to the database instead of the old ones.
     */
    void updateAll(List<? extends Book> books);

    /**
     * This DAO method should delete book entity object from the database.
     * This method should be transactional.
     *
     * @param book is the entity that will be deleted from the database.
     */
    void remove(Book book);

    /**
     * This DAO method should delete list of books entities object from the database.
     * This method should be transactional.
     * This method should uses a batch for multiple queries.
     *
     * @param books is the entities that will be deleted from the database.
     */
    void removeAll(List<? extends Book> books);
}
