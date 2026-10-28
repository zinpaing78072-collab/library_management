package com.example.service;

import com.example.model.BookTransaction;
import com.example.repository.BookTransactionRepository;
import com.example.repository.impl.BookTransactionRepositoryImpl;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BookTransactionService {

    private final BookTransactionRepository repository;

    public BookTransactionService() {
        this(new BookTransactionRepositoryImpl());
    }

    public BookTransactionService(BookTransactionRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public List<BookTransaction> getTransactionsByMemberId(int memberId) throws SQLException {
        return repository.findByMemberId(memberId);
    }

    public List<BookTransaction> getUnreturnedByMemberId(int memberId) throws SQLException {
        List<BookTransaction> all = repository.findByMemberId(memberId);
        List<BookTransaction> unreturned = new ArrayList<>();
        for (BookTransaction tx : all) {
            if (tx.getReturnedDate() == null) {
                unreturned.add(tx);
            }
        }
        return unreturned;
    }

    public List<BookTransaction> findAll() throws SQLException {
        return repository.findAll();
    }

    public BookTransaction issueBook(int memberId, int bookId,
                                     LocalDate issuedDate, LocalDate dueDate) throws SQLException {
        BookTransaction tx = new BookTransaction();
        tx.setMemberId(memberId);
        tx.setBookId(bookId);
        tx.setIssuedDate(issuedDate);
        tx.setDueDate(dueDate);
        tx.setReturnedDate(null);
        tx.setFine(0.0);
        return repository.save(tx);
    }

    public BookTransaction returnBook(int memberId, int bookId, LocalDate returnDate) throws SQLException {
        List<BookTransaction> txs = repository.findByMemberId(memberId);
        BookTransaction tx = null;
        for (BookTransaction t : txs) {
            if (t.getBookId() == bookId && t.getReturnedDate() == null) {
                tx = t;
                break;
            }
        }
        if (tx == null) {
            throw new IllegalArgumentException("No active loan for the given member and book.");
        }
        long daysOverdue = ChronoUnit.DAYS.between(tx.getDueDate(), returnDate);
        double fine = 0.0;
        if (daysOverdue > 0) {
            fine = daysOverdue * 100; // $0.5 per day overdue
        }
        tx.setReturnedDate(returnDate);
        tx.setFine(fine);
        return repository.save(tx);
    }
}
