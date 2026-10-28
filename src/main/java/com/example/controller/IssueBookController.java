package com.example.controller;

import com.example.model.BookTransaction;
import com.example.service.BookTransactionService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class IssueBookController {

    @FXML private TextField txtSearchMemberId;
    @FXML private TableView<BookTransaction> tblTransactions;
    @FXML private TableColumn<BookTransaction, String> colTransactionId;
    @FXML private TableColumn<BookTransaction, String> colBookId;
    @FXML private TableColumn<BookTransaction, String> colIssuedDate;
    @FXML private TableColumn<BookTransaction, String> colDueDate;
    @FXML private TableColumn<BookTransaction, String> colReturnedDate;
    @FXML private TableColumn<BookTransaction, String> colFine;

    @FXML private TextField txtMemberId;
    @FXML private TextField txtBookId;
    @FXML private DatePicker dateIssue;
    @FXML private DatePicker dateDue;
    @FXML private Button btnIssue;
    @FXML private Button btnClear;

    private final ObservableList<BookTransaction> transactions = FXCollections.observableArrayList();
    private BookTransactionService transactionService;

    public void setTransactionService(BookTransactionService service) {
        this.transactionService = service;
    }

    @FXML
    private void initialize() {
        configureColumns();
        tblTransactions.setItems(transactions);
        txtSearchMemberId.textProperty().addListener((observable, oldValue, newValue) -> loadTransactionsForMember());
        loadTransactionsForMember();

        btnIssue.setOnAction(event -> clickIssue());
        btnClear.setOnAction(event -> clickClear());
    }

    private void configureColumns() {
        colTransactionId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getTransactionId())));
        colBookId.setCellValueFactory(cell -> new SimpleStringProperty(String.valueOf(cell.getValue().getBookId())));
        colIssuedDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIssuedDate() == null ? "" : cell.getValue().getIssuedDate().toString()));
        colDueDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDueDate() == null ? "" : cell.getValue().getDueDate().toString()));
        colReturnedDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getReturnedDate() == null ? "" : cell.getValue().getReturnedDate().toString()));
        colFine.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f", cell.getValue().getFine())));
    }

    private void loadTransactionsForMember() {
        String memberIdText = txtSearchMemberId.getText().trim();
        transactions.clear();
        if (memberIdText.isEmpty()) {
            return;
        }
        try {
            int memberId = Integer.parseInt(memberIdText);
            List<BookTransaction> txs = transactionService.getTransactionsByMemberId(memberId);
            transactions.setAll(txs);
        } catch (NumberFormatException e) {
            // ignore invalid numeric input
        } catch (SQLException e) {
            showError("Could not load transactions", e.getMessage());
        }
    }

    @FXML
    private void clickIssue() {
        try {
            int memberId = Integer.parseInt(txtMemberId.getText().trim());
            int bookId = Integer.parseInt(txtBookId.getText().trim());
            LocalDate issuedDate = dateIssue.getValue();
            LocalDate dueDate = dateDue.getValue();

            if (issuedDate == null || dueDate == null) {
                showError("Missing dates", "Please select both issued date and due date.");
                return;
            }

            transactionService.issueBook(memberId, bookId, issuedDate, dueDate);
            clearForm();
            loadTransactionsForMember();
            showInformation("Book issued successfully.");
        } catch (NumberFormatException e) {
            showError("Invalid input", "Member ID and Book ID must be numbers.");
        } catch (SQLException e) {
            showError("Database error", e.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        clearForm();
    }

    private void clearForm() {
        txtMemberId.clear();
        txtBookId.clear();
        dateIssue.setValue(null);
        dateDue.setValue(null);
        tblTransactions.getSelectionModel().clearSelection();
        txtSearchMemberId.clear();
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Issue Book");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
