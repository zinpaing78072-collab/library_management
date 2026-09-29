package com.example.repository;

import com.example.model.Author;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AuthorRepository {

    /** Inserts or updates the author; returns the author with its generated id (inserts only). */
    Author save(Author author) throws SQLException;

    Optional<Author> findById(int authorId) throws SQLException;

    List<Author> findAll() throws SQLException;

    /** Authors whose name contains the given text (case-insensitive). */
    List<Author> findByName(String name) throws SQLException;

    boolean deleteById(int authorId) throws SQLException;

    long count() throws SQLException;
}
