package com.example.model;

import java.time.LocalDate;

public class BookTransaction {
    private int transactionId;
    private int bookId;
    private int memberId;
    private LocalDate issuedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;
    private double fine;

    public BookTransaction() {
    }

    public BookTransaction(int bookId, int memberId,
                           LocalDate issuedDate, LocalDate dueDate,
                           LocalDate returnedDate, double fine) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
        this.fine = fine;
    }

    public BookTransaction(int transactionId, int bookId, int memberId,
                           LocalDate issuedDate, LocalDate dueDate,
                           LocalDate returnedDate, double fine) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.issuedDate = issuedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
        this.fine = fine;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(LocalDate issuedDate) {
        this.issuedDate = issuedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) {
        this.returnedDate = returnedDate;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    @Override
    public String toString() {
        return "BookTransaction{" +
                "transactionId=" + transactionId +
                ", bookId=" + bookId +
                ", memberId=" + memberId +
                ", issuedDate=" + issuedDate +
                ", dueDate=" + dueDate +
                ", returnedDate=" + returnedDate +
                ", fine=" + fine +
                '}';
    }
}