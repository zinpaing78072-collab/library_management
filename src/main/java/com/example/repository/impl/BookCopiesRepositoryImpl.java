package com.example.repository.impl;

import com.example.model.BookCopies;
import com.example.repository.BookCopiesRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class BookCopiesRepositoryImpl extends AbstractJdbcRepository<BookCopies> implements BookCopiesRepository {

    private static final String COLUMNS = "BookCopiesId, BookId, Copies";

    @Override
    public BookCopies save(BookCopies bookCopies) throws SQLException {
        if (bookCopies.getBookCopiesId() == 0) {
            bookCopies.setBookCopiesId(executeInsert(
                    "INSERT INTO BookCopies (BookId, Copies) VALUES (?, ?)",
                    ps -> {
                        ps.setInt(1, bookCopies.getBookId());
                        ps.setInt(2, bookCopies.getCopies());
                    }));
        } else {
            executeUpdate("UPDATE BookCopies SET BookId = ?, Copies = ? WHERE BookCopiesId = ?",
                    ps -> {
                        ps.setInt(1, bookCopies.getBookId());
                        ps.setInt(2, bookCopies.getCopies());
                        ps.setInt(3, bookCopies.getBookCopiesId());
                    });
        }
        return bookCopies;
    }

    @Override
    public Optional<BookCopies> findById(int bookCopiesId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM BookCopies WHERE BookCopiesId = ?", bookCopiesId);
    }

    @Override
    public List<BookCopies> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM BookCopies ORDER BY BookCopiesId");
    }

    @Override
    public List<BookCopies> findByBookId(int bookId) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM BookCopies WHERE BookId = ?", bookId);
    }

    @Override
    public boolean deleteById(int bookCopiesId) throws SQLException {
        return executeUpdate("DELETE FROM BookCopies WHERE BookCopiesId = ?",
                ps -> ps.setInt(1, bookCopiesId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM BookCopies");
    }

    @Override
    protected BookCopies mapRow(ResultSet rs) throws SQLException {
        return new BookCopies(
                rs.getInt("BookCopiesId"),
                rs.getInt("BookId"),
                rs.getInt("Copies"));
    }
}
