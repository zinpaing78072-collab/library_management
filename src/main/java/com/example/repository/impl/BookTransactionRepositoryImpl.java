package com.example.repository.impl;

import com.example.model.BookTransaction;
import com.example.repository.BookTransactionRepository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Optional;

public class BookTransactionRepositoryImpl extends AbstractJdbcRepository<BookTransaction>
        implements BookTransactionRepository {

    private static final String COLUMNS =
            "TransactionId, BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine";
    private static final String INSERT_SQL =
            "INSERT INTO BookTransactions (BookId, MemberId, IssuedDate, DueDate, ReturnedDate, Fine) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE BookTransactions SET BookId = ?, MemberId = ?, IssuedDate = ?, DueDate = ?, "
                    + "ReturnedDate = ?, Fine = ? WHERE TransactionId = ?";

    @Override
    public BookTransaction save(BookTransaction t) throws SQLException {
        if (t.getTransactionId() == 0) {
            t.setTransactionId(executeInsert(INSERT_SQL, ps -> fill(ps, t, 1)));
        } else {
            executeUpdate(UPDATE_SQL, ps -> {
                fill(ps, t, 1);
                ps.setInt(7, t.getTransactionId());
            });
        }
        return t;
    }

    private static void fill(PreparedStatement ps, BookTransaction t, int at) throws SQLException {
        ps.setInt(at, t.getBookId());
        ps.setInt(at + 1, t.getMemberId());
        ps.setDate(at + 2, t.getIssuedDate() == null ? null : Date.valueOf(t.getIssuedDate()));
        ps.setDate(at + 3, t.getDueDate() == null ? null : Date.valueOf(t.getDueDate()));
        if (t.getReturnedDate() == null) {
            ps.setNull(at + 4, Types.DATE);
        } else {
            ps.setDate(at + 4, Date.valueOf(t.getReturnedDate()));
        }
        ps.setDouble(at + 5, t.getFine());
    }

    @Override
    public Optional<BookTransaction> findById(int transactionId) throws SQLException {
        return queryOne("SELECT " + COLUMNS + " FROM BookTransactions WHERE TransactionId = ?", transactionId);
    }

    @Override
    public List<BookTransaction> findAll() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM BookTransactions ORDER BY IssuedDate DESC");
    }

    @Override
    public List<BookTransaction> findByBookId(int bookId) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM BookTransactions WHERE BookId = ? ORDER BY IssuedDate DESC",
                bookId);
    }

    @Override
    public List<BookTransaction> findByMemberId(int memberId) throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM BookTransactions WHERE MemberId = ? ORDER BY IssuedDate DESC",
                memberId);
    }

    @Override
    public List<BookTransaction> findUnreturned() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM BookTransactions "
                + "WHERE ReturnedDate IS NULL ORDER BY DueDate");
    }

    @Override
    public List<BookTransaction> findOverdue() throws SQLException {
        return queryList("SELECT " + COLUMNS + " FROM BookTransactions "
                + "WHERE ReturnedDate IS NULL AND DueDate < CURDATE() ORDER BY DueDate");
    }

    @Override
    public boolean deleteById(int transactionId) throws SQLException {
        return executeUpdate("DELETE FROM BookTransactions WHERE TransactionId = ?",
                ps -> ps.setInt(1, transactionId));
    }

    @Override
    public long count() throws SQLException {
        return queryCount("SELECT COUNT(*) FROM BookTransactions");
    }

    @Override
    protected BookTransaction mapRow(ResultSet rs) throws SQLException {
        return new BookTransaction(
                rs.getInt("TransactionId"),
                rs.getInt("BookId"),
                rs.getInt("MemberId"),
                toLocalDate(rs.getDate("IssuedDate")),
                toLocalDate(rs.getDate("DueDate")),
                toLocalDate(rs.getDate("ReturnedDate")),
                rs.getDouble("Fine"));
    }

    private static java.time.LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
