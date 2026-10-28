package com.example.controller;

import com.example.model.Category;
import com.example.service.CategoryService;
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

/** Controller for the category-management screen. */
public class CategoryController {

    @FXML private TextField txtSearchByName;
    @FXML private TableView<Category> tblCategory;
    @FXML private TableColumn<Category, Number> colId;
    @FXML private TableColumn<Category, String> colName;
    @FXML private TextField txtName;

    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private CategoryService categoryService;

    public void setCategoryService(CategoryService categoryService) {
        this.categoryService = Objects.requireNonNull(categoryService, "categoryService");
    }

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getCategoryId()));
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        tblCategory.setItems(categories);
        tblCategory.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldCategory, selectedCategory) -> {
                    if (selectedCategory != null) {
                        txtName.setText(selectedCategory.getName());
                    }
                });
        txtSearchByName.textProperty().addListener((observable, oldText, newText) -> searchCategories());
        loadCategories();
    }

    private void loadCategories() {
        try {
            categories.setAll(categoryService.getCategories());
        } catch (SQLException exception) {
            showError("Could not load categories", exception.getMessage());
        }
    }

    private void searchCategories() {
        try {
            categories.setAll(categoryService.searchCategories(txtSearchByName.getText()));
        } catch (SQLException exception) {
            showError("Could not search categories", exception.getMessage());
        }
    }

    @FXML
    private void clickSave() {
        try {
            Category selected = tblCategory.getSelectionModel().getSelectedItem();
            Category category = new Category(selected == null ? 0 : selected.getCategoryId(),
                    txtName.getText());
            categoryService.saveCategory(category);
            clearForm();
            searchCategories();
        } catch (IllegalArgumentException | SQLException exception) {
            showError("Could not save the category", exception.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        clearForm();
    }

    private void clearForm() {
        tblCategory.getSelectionModel().clearSelection();
        txtName.clear();
    }

    private void showError(String message, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Category management");
        alert.setHeaderText(null);
        if (detail != null) {
            alert.setContentText(detail);
        }
        alert.showAndWait();
    }
}
