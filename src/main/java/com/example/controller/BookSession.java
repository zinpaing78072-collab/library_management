package com.example.controller;

import com.example.model.Book;
import com.example.service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

/** Shared state and service used by the book screen controllers. */
public final class BookSession {
    private final BookService bookService;
    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private BookTableController tableController;
    private BookFormController formController;

    public BookSession(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "bookService");
    }

    public BookService bookService() {
        return bookService;
    }

    public ObservableList<Book> books() {
        return books;
    }

    public void filterBooks(Integer authorId, Integer categoryId, Integer publisherId) {
        filterBooks(authorId, categoryId, publisherId, false);
    }

    public void filterBooks(Integer authorId, Integer categoryId, Integer publisherId, boolean availableOnly) {
        if (tableController != null) {
            tableController.applyFilters(authorId, categoryId, publisherId, availableOnly);
        }
    }

    public void registerTableController(BookTableController controller) {
        this.tableController = controller;
        if (formController != null) {
            formController.setTableController(controller);
            controller.setSelectionHandler(formController::populateForm);
        }
    }

    public void registerFormController(BookFormController controller) {
        this.formController = controller;
        if (tableController != null) {
            controller.setTableController(tableController);
            tableController.setSelectionHandler(controller::populateForm);
        }
    }

    public void clearForm() {
        if (formController != null) {
            formController.clearForm();
        }
    }
}
