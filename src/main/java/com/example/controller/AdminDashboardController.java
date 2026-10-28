package com.example.controller;

import com.example.model.Book;
import com.example.model.BookTransaction;
import com.example.service.BookService;
import com.example.service.BookTransactionService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/** Controller for the admin dashboard showing book borrow statistics. */
public class AdminDashboardController {

    @FXML private AnchorPane anchor;
    @FXML private BarChart<String, Number> BookBarChart;
    @FXML private PieChart BookPieChart;

    private BookService bookService;
    private BookTransactionService transactionService;

    public void setBookService(BookService bookService) {
        this.bookService = Objects.requireNonNull(bookService, "bookService");
    }

    public void setTransactionService(BookTransactionService transactionService) {
        this.transactionService = Objects.requireNonNull(transactionService, "transactionService");
    }

    @FXML
    private void initialize() {
        try {
            loadDashboardData();
        } catch (SQLException exception) {
            throw new IllegalStateException("Could not load dashboard data", exception);
        }
    }

    private void loadDashboardData() throws SQLException {
        List<BookTransaction> transactions = transactionService.findAll();
        List<Book> books = bookService.getBooks();

        // Count borrows per bookId
        Map<Integer, Long> borrowCountByBookId = new HashMap<>();
        for (BookTransaction tx : transactions) {
            borrowCountByBookId.merge(tx.getBookId(), 1L, Long::sum);
        }

        // Build bookId -> title map
        Map<Integer, String> bookTitles = new HashMap<>();
        for (Book book : books) {
            bookTitles.put(book.getBookId(), book.getTitle());
        }

        // Sort by borrow count descending, take top 10
        List<Map.Entry<Integer, Long>> sorted = borrowCountByBookId.entrySet().stream()
                .sorted(Map.Entry.<Integer, Long>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        // BarChart: book name -> borrow count
        var barData = FXCollections.<XYChart.Data<String, Number>>observableArrayList();
        for (Map.Entry<Integer, Long> entry : sorted) {
            String title = bookTitles.getOrDefault(entry.getKey(),
                    "Unknown Book (" + entry.getKey() + ")");
            barData.add(new XYChart.Data<>(title, entry.getValue()));
        }
        BookBarChart.getData().add(new XYChart.Series<>("Books Borrowed", barData));

        // PieChart: borrow distribution
        var pieData = FXCollections.<PieChart.Data>observableArrayList();
        for (Map.Entry<Integer, Long> entry : sorted) {
            String title = bookTitles.getOrDefault(entry.getKey(),
                    "Unknown Book (" + entry.getKey() + ")");
            pieData.add(new PieChart.Data(title, entry.getValue()));
        }
        BookPieChart.setData(pieData);
    }
}