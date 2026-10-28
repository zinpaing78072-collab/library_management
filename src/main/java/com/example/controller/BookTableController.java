package com.example.controller;

import com.example.model.Book;
import com.example.service.BookService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.sql.SQLException;

/** Controller for the book table component. */
public class BookTableController {
    @FXML private TableView<Book> tblBook;
    @FXML private TableColumn<Book, Number> colBID;
    @FXML private TableColumn<Book, String> colTitle;
    @FXML private TableColumn<Book, String> colISBN;
    @FXML private TableColumn<Book, Number> colCopies;
    @FXML private TableColumn<Book, String> colDescription;
    @FXML private TableColumn<Book, String> colImage;
    @FXML private TableColumn<Book, Number> colPYear;

    private BookService bookService;
    private ObservableList<Book> books;
    private java.util.function.Consumer<Book> selectionHandler;
    private Integer authorId;
    private Integer categoryId;
    private Integer publisherId;
    private boolean availableOnly;

    public void setSession(BookSession session) {
        this.bookService = session.bookService();
        this.books = session.books();
        session.registerTableController(this);
    }

    public void setSelectionHandler(java.util.function.Consumer<Book> selectionHandler) {
        this.selectionHandler = selectionHandler;
    }

    @FXML
    private void initialize() {
        configureColumns();
        tblBook.setItems(books);
        tblBook.getSelectionModel().selectedItemProperty().addListener((observable, oldBook, selectedBook) -> {
            if (selectedBook != null && selectionHandler != null) {
                selectionHandler.accept(selectedBook);
            }
        });
        loadBooks();
    }

    public void applyFilters(Integer authorId, Integer categoryId, Integer publisherId) {
        applyFilters(authorId, categoryId, publisherId, false);
    }

    public void applyFilters(Integer authorId, Integer categoryId, Integer publisherId, boolean availableOnly) {
        this.authorId = authorId;
        this.categoryId = categoryId;
        this.publisherId = publisherId;
        this.availableOnly = availableOnly;
        loadBooks();
    }

    public void loadBooks() {
        try {
            books.setAll(bookService.getBooks(authorId, categoryId, publisherId, availableOnly));
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load books from LibraryManagementSystemDB.", exception);
        }
    }

    public Book selectedBook() {
        return tblBook.getSelectionModel().getSelectedItem();
    }

    public void clearSelection() {
        tblBook.getSelectionModel().clearSelection();
    }

    private void configureColumns() {
        colBID.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getBookId()));
        colTitle.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTitle()));
        colISBN.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIsbn()));
        colCopies.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getCopies()));
        colDescription.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDescription()));
        colImage.setCellValueFactory(cell -> new SimpleStringProperty(storedImageName(cell.getValue().getImage())));
        colPYear.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPublishedYear()));
    }

    private static String storedImageName(String storedPath) {
        return storedPath == null || storedPath.isBlank() ? storedPath : new java.io.File(storedPath).getName();
    }
}
