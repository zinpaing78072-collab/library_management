package com.example.service;

import com.example.model.Author;
import com.example.model.Book;
import com.example.model.Category;
import com.example.model.Publisher;
import com.example.repository.AuthorRepository;
import com.example.repository.BookRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.PublisherRepository;
import com.example.repository.impl.AuthorRepositoryImpl;
import com.example.repository.impl.BookRepositoryImpl;
import com.example.repository.impl.CategoryRepositoryImpl;
import com.example.repository.impl.PublisherRepositoryImpl;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Application service for the book-management screen. */
public class BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final PublisherRepository publisherRepository;

    public BookService() {
        this(new BookRepositoryImpl(), new AuthorRepositoryImpl(), new CategoryRepositoryImpl(), new PublisherRepositoryImpl());
    }

    public BookService(BookRepository bookRepository, AuthorRepository authorRepository,
            CategoryRepository categoryRepository, PublisherRepository publisherRepository) {
        this.bookRepository = Objects.requireNonNull(bookRepository, "bookRepository");
        this.authorRepository = Objects.requireNonNull(authorRepository, "authorRepository");
        this.categoryRepository = Objects.requireNonNull(categoryRepository, "categoryRepository");
        this.publisherRepository = Objects.requireNonNull(publisherRepository, "publisherRepository");
    }

    public List<Book> getBooks() throws SQLException {
        return bookRepository.findAll();
    }

    public List<Book> getBooks(Integer authorId, Integer categoryId, Integer publisherId) throws SQLException {
        return getBooks(authorId, categoryId, publisherId, false);
    }

    public List<Book> getBooks(Integer authorId, Integer categoryId, Integer publisherId, boolean availableOnly) throws SQLException {
        return bookRepository.findByFilters(authorId, categoryId, publisherId, availableOnly);
    }

    public List<Author> getAuthors() throws SQLException {
        return authorRepository.findAll();
    }

    public List<Category> getCategories() throws SQLException {
        return categoryRepository.findAll();
    }

    public List<Publisher> getPublishers() throws SQLException {
        return publisherRepository.findAll();
    }

    public Book saveBook(Book book) throws SQLException {
        validate(book);
        Optional<Book> sameIsbn = bookRepository.findByIsbn(book.getIsbn());
        if (sameIsbn.isPresent() && sameIsbn.get().getBookId() != book.getBookId()) {
            throw new IllegalArgumentException("A book with this ISBN already exists.");
        }
        return bookRepository.save(book);
    }

    public Book updateBook(Book book) throws SQLException {
        validate(book);
        Optional<Book> sameIsbn = bookRepository.findByIsbn(book.getIsbn());
        if (sameIsbn.isPresent() && sameIsbn.get().getBookId() != book.getBookId()) {
            throw new IllegalArgumentException("A book with this ISBN already exists.");
        }
        Optional<Book> existing = bookRepository.findById(book.getBookId());
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Book with id " + book.getBookId() + " does not exist.");
        }
        return bookRepository.save(book);
    }

    public void deleteBook(int bookId) throws SQLException {
        Optional<Book> existing = bookRepository.findById(bookId);
        if (existing.isEmpty()) {
            throw new IllegalArgumentException("Book with id " + bookId + " does not exist.");
        }
        bookRepository.deleteById(bookId);
    }

    private void validate(Book book) throws SQLException {
        if (book == null) {
            throw new IllegalArgumentException("Book is required.");
        }
        requireText(book.getTitle(), "Title");
        requireText(book.getIsbn(), "ISBN");
        if (book.getPublishedYear() < 1000 || book.getPublishedYear() > 9999) {
            throw new IllegalArgumentException("Published year must be a four-digit year.");
        }
        if (book.getCopies() < 0) {
            throw new IllegalArgumentException("Copies cannot be negative.");
        }
        if (authorRepository.findById(book.getAuthorId()).isEmpty()) {
            throw new IllegalArgumentException("Select a valid author.");
        }
        if (categoryRepository.findById(book.getCategoryId()).isEmpty()) {
            throw new IllegalArgumentException("Select a valid category.");
        }
        if (publisherRepository.findById(book.getPublisherId()).isEmpty()) {
            throw new IllegalArgumentException("Select a valid publisher.");
        }
    }

    private static void requireText(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " is required.");
        }
    }
}

