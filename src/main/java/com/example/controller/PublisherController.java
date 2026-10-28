package com.example.controller;

import com.example.model.Publisher;
import com.example.service.PublisherService;
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

/** Controller for the publisher-management screen. */
public class PublisherController {

    @FXML private TextField txtSearchByName;
    @FXML private TableView<Publisher> tblPublisher;
    @FXML private TableColumn<Publisher, Number> colId;
    @FXML private TableColumn<Publisher, String> colName;
    @FXML private TextField txtName;

    private final ObservableList<Publisher> publishers = FXCollections.observableArrayList();
    private PublisherService publisherService;

    public void setPublisherService(PublisherService publisherService) {
        this.publisherService = Objects.requireNonNull(publisherService, "publisherService");
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getPublisherId()));
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        tblPublisher.setItems(publishers);
        tblPublisher.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldPublisher, selectedPublisher) -> {
                    if (selectedPublisher != null) {
                        txtName.setText(selectedPublisher.getName());
                    }
                });
        txtSearchByName.textProperty().addListener((observable, oldText, newText) -> searchPublishers());
        loadPublishers();
    }

    private void loadPublishers() {
        try {
            publishers.setAll(publisherService.getPublishers());
        } catch (SQLException exception) {
            showError("Could not load publishers", exception.getMessage());
        }
    }

    private void searchPublishers() {
        try {
            publishers.setAll(publisherService.searchPublishers(txtSearchByName.getText()));
        } catch (SQLException exception) {
            showError("Could not search publishers", exception.getMessage());
        }
    }

    @FXML
    private void clickSave() {
        try {
            Publisher selected = tblPublisher.getSelectionModel().getSelectedItem();
            Publisher publisher = new Publisher(selected == null ? 0 : selected.getPublisherId(),
                    txtName.getText());
            publisherService.savePublisher(publisher);
            clearForm();
            searchPublishers();
        } catch (IllegalArgumentException | SQLException exception) {
            showError("Could not save the publisher", exception.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        clearForm();
    }

    private void clearForm() {
        tblPublisher.getSelectionModel().clearSelection();
        txtName.clear();
    }

    private void showError(String message, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Publisher management");
        alert.setHeaderText(null);
        if (detail != null) {
            alert.setContentText(detail);
        }
        alert.showAndWait();
    }
}
