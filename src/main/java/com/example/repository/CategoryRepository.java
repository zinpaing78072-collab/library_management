package com.example.repository;

import com.example.model.Category;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {

    /** Inserts or updates the category; returns it with its generated id (inserts only). */
    Category save(Category category) throws SQLException;

    Optional<Category> findById(int categoryId) throws SQLException;

    List<Category> findAll() throws SQLException;

    /** Categories whose name contains the given text (case-insensitive). */
    List<Category> findByName(String name) throws SQLException;

    boolean deleteById(int categoryId) throws SQLException;

    long count() throws SQLException;
}
