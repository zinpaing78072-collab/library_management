package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Provides a single shared JDBC connection to the LibraryManagementSystemDB
 * MySQL database.
 *
 * The MySQL driver (com.mysql:mysql-connector-j) is loaded automatically
 * via the JDBC 4+ service-provider mechanism — no Class.forName() needed.
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/LibraryManagementSystemDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static Connection connection;

    private DatabaseConnection() {
        // static utility class — no instances
    }

    /**
     * Returns the shared connection, opening (or re-opening) it lazily.
     *
     * @throws SQLException if the database is unreachable or credentials fail
     */
    public static synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to LibraryManagementSystemDB");
        }
        return connection;
    }

    /** Closes the shared connection if it is open. Safe to call repeatedly. */
    public static synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}
