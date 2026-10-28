package com.example.controller;

import com.example.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.util.Objects;
import java.util.function.Consumer;

/** Controller for the librarian dashboard: sidebar navigation and logout. */
public class LibrarianController {

    @FXML private Label lblWelcome;
    @FXML private AnchorPane librarianPane;
    @FXML private Button btnCategory;
    @FXML private Button btnAuthor;
    @FXML private Button btnPublisher;
    @FXML private Button btnBook;
    @FXML private Button btnMember;
    @FXML private Button btnLogout;

    private User user;
    /** Opens a screen by FXML name (e.g. "/Book.fxml"); supplied by the entry point. */
    private Consumer<String> screenLoader;
    /** Returns to the login screen; supplied by the entry point. */
    private Runnable logoutHandler;

    public void setUser(User user) {
        this.user = Objects.requireNonNull(user, "user");
    }

    public void setScreenLoader(Consumer<String> screenLoader) {
        this.screenLoader = Objects.requireNonNull(screenLoader, "screenLoader");
    }

    public void setLogoutHandler(Runnable logoutHandler) {
        this.logoutHandler = Objects.requireNonNull(logoutHandler, "logoutHandler");
    }

    @FXML
    private void initialize() {
        if (user != null) {
            lblWelcome.setText("Welcome, " + user.getName());
        }
    }

    /** The content pane where the selected screen is displayed. */
    public AnchorPane getLibrarianPane() {
        return librarianPane;
    }

    @FXML
    private void clickCategory() {
        showScreen("/Category.fxml");
    }

    @FXML
    private void clickAuthor() {
        showScreen("/Author.fxml");
    }

    @FXML
    private void clickPublisher() {
        showScreen("/Publisher.fxml");
    }

    @FXML
    private void clickBook() {
        showScreen("/Book.fxml");
    }

    @FXML
    private void clickMember() {
        showScreen("/Member.fxml");
    }

    @FXML
    private void clickLogout() {
        logoutHandler.run();
    }

    @FXML
    private void clickIssue() {
        showScreen("/IssueBook.fxml");
    }

    @FXML
    private void clickReturn() {
        showScreen("/ReturnBook.fxml");
    }


    /** Opens an FXML screen in the content pane; falls back to a placeholder when the
     *  screen has not been built yet. */
    private void showScreen(String fxmlPath) {
        if (LibrarianController.class.getResource(fxmlPath) == null) {
            showPlaceholder(fxmlPath);
            return;
        }
        screenLoader.accept(fxmlPath);
    }

    private void showPlaceholder(String fxmlPath) {
        String name = fxmlPath.replace("/", "").replace(".fxml", "");
        Label placeholder = new Label(name + " screen is not available yet.");
        placeholder.setLayoutX(24.0);
        placeholder.setLayoutY(24.0);
        placeholder.getStyleClass().add("label-title");
        librarianPane.getChildren().setAll(placeholder);
    }
}

