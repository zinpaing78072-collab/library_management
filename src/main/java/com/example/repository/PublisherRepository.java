package com.example.repository;

import com.example.model.Publisher;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface PublisherRepository {

    /** Inserts or updates the publisher; returns it with its generated id (inserts only). */
    Publisher save(Publisher publisher) throws SQLException;

    Optional<Publisher> findById(int publisherId) throws SQLException;

    List<Publisher> findAll() throws SQLException;

    /** Publishers whose name contains the given text (case-insensitive). */
    List<Publisher> findByName(String name) throws SQLException;

    boolean deleteById(int publisherId) throws SQLException;

    long count() throws SQLException;
}
