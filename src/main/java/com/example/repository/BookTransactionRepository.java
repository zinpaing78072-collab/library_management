package com.example.repository;

import com.example.model.BookTransaction;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface BookTransactionRepository {

    /** Inserts or updates the transaction; returns it with its generated id (inserts only). */
    BookTransaction save(BookTransaction transaction) throws SQLException;

    Optional<BookTransaction> findById(int transactionId) throws SQLException;

    List<BookTransaction> findAll() throws SQLException;

    List<BookTransaction> findByBookId(int bookId) throws SQLException;

    List<BookTransaction> findByMemberId(int memberId) throws SQLException;

    /** Loans that have not been returned yet (returnedDate is NULL). */
    List<BookTransaction> findUnreturned() throws SQLException;

    /** Loans past their due date that have not been returned yet. */
    List<BookTransaction> findOverdue() throws SQLException;

    boolean deleteById(int transactionId) throws SQLException;

    long count() throws SQLException;
}
