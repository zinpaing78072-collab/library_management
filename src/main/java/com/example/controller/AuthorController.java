package com.example.controller;

import com.example.model.Author;
import com.example.service.AuthorService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Objects;

/** Controller for the author-management screen. */
public class AuthorController {

    @FXML private TextField txtSearchByName;
    @FXML private TableView<Author> tblAuthor;
    @FXML private TableColumn<Author, Number> colId;
    @FXML private TableColumn<Author, String> colName;
    @FXML private TextField txtName;

    private final ObservableList<Author> authors = FXCollections.observableArrayList();
    private AuthorService authorService;

    public void setAuthorService(AuthorService authorService) {
        this.authorService = Objects.requireNonNull(authorService, "authorService");
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getAuthorId()));
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        tblAuthor.setItems(authors);
        tblAuthor.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldAuthor, selectedAuthor) -> {
                    if (selectedAuthor != null) {
                        txtName.setText(selectedAuthor.getName());
                    }
                });
        txtSearchByName.textProperty().addListener((observable, oldText, newText) -> searchAuthors());
        loadAuthors();
    }

    private void loadAuthors() {
        try {
            authors.setAll(authorService.getAuthors());
        } catch (SQLException exception) {
            showError("Could not load authors", exception.getMessage());
        }
    }

    private void searchAuthors() {
        try {
            authors.setAll(authorService.searchAuthors(txtSearchByName.getText()));
        } catch (SQLException exception) {
            showError("Could not search authors", exception.getMessage());
        }
    }

    @FXML
    private void clickSave() {
        try {
            Author selected = tblAuthor.getSelectionModel().getSelectedItem();
            Author author = new Author(selected == null ? 0 : selected.getAuthorId(),
                    txtName.getText());
            authorService.saveAuthor(author);
            clearForm();
            searchAuthors();
        } catch (IllegalArgumentException | SQLException exception) {
            showError("Could not save the author", exception.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        clearForm();
    }

    private void clearForm() {
        tblAuthor.getSelectionModel().clearSelection();
        txtName.clear();
    }

    private void showError(String message, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Author management");
        alert.setHeaderText(null);
        if (detail != null) {
            alert.setContentText(detail);
        }
        alert.showAndWait();
    }
}
