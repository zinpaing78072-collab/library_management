package com.example.controller;

import com.example.model.Author;
import com.example.model.Category;
import com.example.model.Publisher;
import com.example.service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/** Controller for the top-level book screen and its search filters. */
public class BookController {
    /**
     * Id of the synthetic "All ..." entry. It is never stored in the database;
     * it only marks the filter as unset, which is expressed as a null id.
     */
    private static final int ALL_ID = 0;

    @FXML private ComboBox<Author> cbAuthorFilter;
    @FXML private ComboBox<Category> cbCategoryFilter;
    @FXML private ComboBox<Publisher> cbPublisherFilter;

    private final ObservableList<Author> authors = FXCollections.observableArrayList();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableList<Publisher> publishers = FXCollections.observableArrayList();
    private BookService bookService;
    private BookSession session;

    public void setSession(BookSession session) {
        this.session = Objects.requireNonNull(session, "session");
        this.bookService = session.bookService();
    }

    @FXML
    private void initialize() {
        loadFilterOptions();
        cbAuthorFilter.setItems(authors);
        cbCategoryFilter.setItems(categories);
        cbPublisherFilter.setItems(publishers);
        cbAuthorFilter.setPromptText("All authors");
        cbCategoryFilter.setPromptText("All categories");
        cbPublisherFilter.setPromptText("All publishers");

        // Start on the "All ..." row (the first element of each list) so the initial
        // view is unfiltered and there is always a visible way back to it. The
        // instance is taken from the list so the row is highlighted, not just shown.
        cbAuthorFilter.setValue(authors.get(0));
        cbCategoryFilter.setValue(categories.get(0));
        cbPublisherFilter.setValue(publishers.get(0));

        cbAuthorFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cbCategoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        cbPublisherFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }

    private static Author allAuthors() {
        return new Author(ALL_ID, "All authors");
    }

    private static Category allCategories() {
        return new Category(ALL_ID, "All categories");
    }

    private static Publisher allPublishers() {
        return new Publisher(ALL_ID, "All publishers");
    }

    private void loadFilterOptions() {
        try {
            List<Author> loadedAuthors = bookService.getAuthors();
            List<Category> loadedCategories = bookService.getCategories();
            List<Publisher> loadedPublishers = bookService.getPublishers();
            // The "All ..." row is prepended so it is the first entry in the dropdown.
            authors.setAll(allAuthors());
            authors.addAll(loadedAuthors);
            categories.setAll(allCategories());
            categories.addAll(loadedCategories);
            publishers.setAll(allPublishers());
            publishers.addAll(loadedPublishers);
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load search options.", exception);
        }
    }

    private void applyFilters() {
        Author author = cbAuthorFilter.getValue();
        Category category = cbCategoryFilter.getValue();
        Publisher publisher = cbPublisherFilter.getValue();
        session.filterBooks(filterId(author == null ? 0 : author.getAuthorId()),
                filterId(category == null ? 0 : category.getCategoryId()),
                filterId(publisher == null ? 0 : publisher.getPublisherId()));
    }

    /** Maps the synthetic "All ..." id (and an unset combo box) to a null filter. */
    private static Integer filterId(int id) {
        return id == ALL_ID ? null : id;
    }
}
