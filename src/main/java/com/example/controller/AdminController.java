package com.example.controller;

import com.example.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

/** Controller for the admin dashboard: sidebar navigation and logout. */
public class AdminController {

    @FXML private Label welcome;
    @FXML private AnchorPane anchor;
    @FXML private Button dash;
    @FXML private Button acc;
    @FXML private Button logout;

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
            welcome.setText("Welcome, " + user.getName());
        }
    }

    /** The content pane where the selected screen is displayed. */
    public AnchorPane getAnchor() {
        return anchor;
    }

    @FXML
    private void clickDash() {
        showScreen("/Book.fxml");
    }

    @FXML
    private void clickAcc() {
        showScreen("/User.fxml");
    }

    @FXML
    private void clickLogout() {
        logoutHandler.run();
    }

    /** Opens an FXML screen in the content pane; falls back to a placeholder when the
     *  screen has not been built yet. */
    private void showScreen(String fxmlPath) {
        if (AdminController.class.getResource(fxmlPath) == null) {
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
        anchor.getChildren().setAll(placeholder);
    }
}