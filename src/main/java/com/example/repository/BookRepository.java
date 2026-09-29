package com.example.repository;

import com.example.model.Book;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookRepository {

    /** Inserts or updates the book; returns the book with its generated id (inserts only). */
    Book save(Book book) throws SQLException;

    Optional<Book> findById(int bookId) throws SQLException;

    List<Book> findAll() throws SQLException;

    /** Books whose title contains the given text (case-insensitive). */
    List<Book> findByTitle(String title) throws SQLException;

    Optional<Book> findByIsbn(String isbn) throws SQLException;

    List<Book> findByAuthorId(int authorId) throws SQLException;

    List<Book> findByCategoryId(int categoryId) throws SQLException;

    /** Books matching all supplied filters; null values are ignored. */
    List<Book> findByFilters(Integer authorId, Integer categoryId, Integer publisherId) throws SQLException;

    boolean deleteById(int bookId) throws SQLException;

    long count() throws SQLException;
}
