package com.example.repository.impl;

import com.example.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Shared JDBC plumbing for the repository implementations. Concrete repositories
 * supply the SQL and map rows to models; this class handles connection, statement
 * and result-set handling so each query is a one-liner.
 */
public abstract class AbstractJdbcRepository<T> {

    /** Maps the current row of a result set to a model object. */
    protected abstract T mapRow(ResultSet rs) throws SQLException;

    /** Runs a SELECT and maps every returned row. */
    protected List<T> queryList(String sql, Object... params) throws SQLException {
        List<T> results = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = prepare(conn, sql, params);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        }
        return results;
    }

    /** Runs a SELECT expected to return at most one row. */
    protected Optional<T> queryOne(String sql, Object... params) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = prepare(conn, sql, params);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
        }
    }

    /** Runs an UPDATE/DELETE; returns true when at least one row was affected. */
    protected boolean executeUpdate(String sql, Binder binder) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = prepare(conn, sql)) {
            binder.bind(ps);
            return ps.executeUpdate() > 0;
        }
    }

    /** Runs an INSERT and returns the generated key (0 when none is produced). */
    protected int executeInsert(String sql, Binder binder) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            binder.bind(ps);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    /** Runs a SELECT COUNT(*)-style query. */
    protected long queryCount(String sql) throws SQLException {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = prepare(conn, sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getLong(1) : 0;
        }
    }

    private PreparedStatement prepare(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
        return ps;
    }

    /** Sets the parameters of a statement (lambdas like {@code ps -> ps.setString(1, ...)}). */
    @FunctionalInterface
    protected interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }
}
