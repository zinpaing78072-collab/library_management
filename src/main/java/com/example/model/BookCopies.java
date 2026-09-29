package com.example.model;

public class BookCopies {
    private int bookCopiesId;
    private int bookId;
    private int copies;

    public BookCopies() {
    }

    public BookCopies(int bookId, int copies) {
        this.bookId = bookId;
        this.copies = copies;
    }

    public BookCopies(int bookCopiesId, int bookId, int copies) {
        this.bookCopiesId = bookCopiesId;
        this.bookId = bookId;
        this.copies = copies;
    }

    public int getBookCopiesId() {
        return bookCopiesId;
    }

    public void setBookCopiesId(int bookCopiesId) {
        this.bookCopiesId = bookCopiesId;
    }

    public int getBookId() {
        return bookId;
    }

    public void setBookId(int bookId) {
        this.bookId = bookId;
    }

    public int getCopies() {
        return copies;
    }

    public void setCopies(int copies) {
        this.copies = copies;
    }

    @Override
    public String toString() {
        return "BookCopies{" +
                "bookCopiesId=" + bookCopiesId +
                ", bookId=" + bookId +
                ", copies=" + copies +
                '}';
    }
}
