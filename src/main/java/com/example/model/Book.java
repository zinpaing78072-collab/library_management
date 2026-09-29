package com.example.model;

public class Book {
    private int bookId;
    private String title;
    private String isbn;
    private int authorId;
    private int publisherId;
    private int publishedYear;
    private int categoryId;
    private String description;
    private int copies;
    private String image;
    
    public Book(int bookId, String title, String isbn, int authorId, int publisherId, int publishedYear, int categoryId,
            String description, int copies, String image) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.authorId = authorId;
        this.publisherId = publisherId;
        this.publishedYear = publishedYear;
        this.categoryId = categoryId;
        this.description = description;
        this.copies = copies;
        this.image = image;
    }
    public int getBookId() {
        return bookId;
    }
    public void setBookId(int bookId) {
        this.bookId = bookId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getIsbn() {
        return isbn;
    }
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    public int getAuthorId() {
        return authorId;
    }
    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }
    public int getPublisherId() {
        return publisherId;
    }
    public void setPublisherId(int publisherId) {
        this.publisherId = publisherId;
    }
    public int getPublishedYear() {
        return publishedYear;
    }
    public void setPublishedYear(int publishedYear) {
        this.publishedYear = publishedYear;
    }
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getCopies() {
        return copies;
    }
    public void setCopies(int copies) {
        this.copies = copies;
    }
    public String getImage() {
        return image;
    }
    public void setImage(String image) {
        this.image = image;
    }
    @Override
    public String toString() {
        return "Book [bookId=" + bookId + ", title=" + title + ", isbn=" + isbn + ", authorId=" + authorId
                + ", publisherId=" + publisherId + ", publishedYear=" + publishedYear + ", categoryId=" + categoryId
                + ", description=" + description + ", copies=" + copies + ", image=" + image + "]";
    }

}
