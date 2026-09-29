package com.example.repository.impl;

import com.example.model.Author;
import com.example.repository.AuthorRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class AuthorRepositoryImpl extends AbstractJdbcRepository<Author> implements AuthorRepository {

    private static final String COLUMNS = "AuthorId, Name";

    @Override
    public Author save(Author author) throws SQLException {
        if (author.getAuthorId() == 0) {
            author.setAuthorId(executeInsert("INSERT INTO Author (Name) VALUES (?)",
                    ps -> ps.setString(1, author.getName())));
        } else {
            executeUpdate("UPDATE Author SET Name = ? WHERE AuthorId = ?",
                    ps -> {
                        ps.setString(1, author.getName());
                        ps.setInt(2, author.getAuthorId());
                    });
        }
        return author;
    }

    @Override
    public Optional<Author> findById(int authorId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM Author WHERE AuthorId = ?", authorId);
    }

    @Override
    public List<Author> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Author ORDER BY Name");
    }

    @Override
    public List<Author> findByName(String name) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM Author WHERE Name LIKE ? ORDER BY Name",
                "%" + name + "%");
    }

    @Override
    public boolean deleteById(int authorId) throws SQLException {
        return executeUpdate("DELETE FROM Author WHERE AuthorId = ?",
                ps -> ps.setInt(1, authorId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM Author");
    }

    @Override
    protected Author mapRow(ResultSet rs) throws SQLException {
        return new Author(rs.getInt("AuthorId"), rs.getString("Name"));
    }
}
