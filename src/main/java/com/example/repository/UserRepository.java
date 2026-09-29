package com.example.repository;

import com.example.model.User;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserRepository {

    /** Inserts or updates the user; returns the user with its generated id (inserts only).
     *  Never return/handle the password outside the persistence layer. */
    User save(User user) throws SQLException;

    Optional<User> findById(int userId) throws SQLException;

    List<User> findAll() throws SQLException;

    /** Users whose name contains the given text (case-insensitive). */
    List<User> findByName(String name) throws SQLException;

    Optional<User> findByEmail(String email) throws SQLException;

    /** Credential check for the login screen; returns empty when no match. */
    Optional<User> login(String name, String password) throws SQLException;

    List<User> findByRole(String role) throws SQLException;

    boolean deleteById(int userId) throws SQLException;

    long count() throws SQLException;
}
