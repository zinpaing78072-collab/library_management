package com.example.repository;

import com.example.model.BookCopies;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookCopiesRepository {

    /** Inserts or updates the copy record; returns it with its generated id (inserts only). */
    BookCopies save(BookCopies bookCopies) throws SQLException;

    Optional<BookCopies> findById(int bookCopiesId) throws SQLException;

    List<BookCopies> findAll() throws SQLException;

    List<BookCopies> findByBookId(int bookId) throws SQLException;

    boolean deleteById(int bookCopiesId) throws SQLException;

    long count() throws SQLException;
}
