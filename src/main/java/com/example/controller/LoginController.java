package com.example.controller;

import com.example.model.User;
import com.example.service.LoginService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;

/** Controller for the login screen. */
public class LoginController {

    @FXML private ImageView imgHome;
    @FXML private TextField txtUserName;
    @FXML private PasswordField pwdPassword;
    @FXML private RadioButton rbAdmin;
    @FXML private RadioButton rbLibrarian;
    @FXML private Label lblMessage;

    private final ToggleGroup roleGroup = new ToggleGroup();
    private LoginService loginService;
    private User loggedInUser;
    private java.util.function.Consumer<User> onLoginSuccess;

    /** Injected by the entry point; falls back to the default service when run standalone. */
    public void setLoginService(LoginService loginService) {
        this.loginService = Objects.requireNonNull(loginService, "loginService");
    }

    /** The user that authenticated successfully; null until login succeeds. */
    public User getLoggedInUser() {
        return loggedInUser;
    }

    /** Invoked by the entry point with the authenticated user so it can open the matching screen. */
    public void setOnLoginSuccess(java.util.function.Consumer<User> onLoginSuccess) {
        this.onLoginSuccess = Objects.requireNonNull(onLoginSuccess, "onLoginSuccess");
    }

    @FXML
    private void initialize() {
        rbAdmin.setToggleGroup(roleGroup);
        rbLibrarian.setToggleGroup(roleGroup);
        rbAdmin.setSelected(true);
        if (loginService == null) {
            loginService = new LoginService();
        }
        loadImage();
        pwdPassword.setOnAction(event -> clickLogin());
    }

    @FXML
    private void clickLogin() {
        try {
            String role = selectedRole();
            loggedInUser = loginService.login(txtUserName.getText(), pwdPassword.getText(), role);
            lblMessage.getStyleClass().remove("login-message-error");
            lblMessage.getStyleClass().add("login-message-success");
            lblMessage.setText("Welcome, " + loggedInUser.getName() + ".");
            if (onLoginSuccess != null) {
                onLoginSuccess.accept(loggedInUser);
            }
        } catch (IllegalArgumentException | SQLException exception) {
            loggedInUser = null;
            lblMessage.getStyleClass().remove("login-message-success");
            lblMessage.getStyleClass().add("login-message-error");
            lblMessage.setText(exception.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        txtUserName.clear();
        pwdPassword.clear();
        rbAdmin.setSelected(true);
        loggedInUser = null;
        lblMessage.getStyleClass().removeAll("login-message-error", "login-message-success");
        lblMessage.setText("");
        txtUserName.requestFocus();
    }

    private String selectedRole() {
        if (rbAdmin.isSelected()) {
            return "Admin";
        }
        if (rbLibrarian.isSelected()) {
            return "Librarian";
        }
        throw new IllegalArgumentException("Select your role.");
    }

    private void loadImage() {
        URL resource = LoginController.class.getResource("/images/logo.jpg");
        if (resource != null) {
            imgHome.setImage(new Image(resource.toExternalForm()));
        }
    }
}
