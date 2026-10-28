package com.example.controller;

import com.example.model.User;
import com.example.service.UserService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/** Controller for the user-management screen. */
public class UserController {
    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final String IMAGE_DIRECTORY_PROPERTY = "library.management.image.directory";

    @FXML private TextField txtSearchByName;
    @FXML private TableView<User> tblUser;
    @FXML private TableColumn<User, String> colName;
    @FXML private TableColumn<User, String> colPassword;
    @FXML private TableColumn<User, String> colDOB;
    @FXML private TableColumn<User, String> colGender;
    @FXML private TableColumn<User, String> colEmail;
    @FXML private TableColumn<User, String> colPhone;
    @FXML private TableColumn<User, String> colRole;
    @FXML private TableColumn<User, String> colAddress;
    @FXML private TableColumn<User, String> colJoinDate;
    @FXML private TableColumn<User, String> colImage;

    @FXML private TextField txtUserName;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhoneNo;
    @FXML private TextArea txtAddress;
    @FXML private DatePicker dateDOB;
    @FXML private DatePicker dateJoin;
    @FXML private RadioButton rbtMale;
    @FXML private RadioButton rbtFemale;
    @FXML private ComboBox<String> cbRole;
    @FXML private ImageView imgUser;

    private final ObservableList<User> users = FXCollections.observableArrayList();
    private final ToggleGroup genderGroup = new ToggleGroup();
    private UserService userService;
    private File selectedImageFile;

    public void setUserService(UserService userService) {
        this.userService = Objects.requireNonNull(userService, "userService");
    }

    @FXML
    private void initialize() {
        rbtMale.setToggleGroup(genderGroup);
        rbtFemale.setToggleGroup(genderGroup);
        cbRole.setItems(FXCollections.observableArrayList(UserService.ROLES));
        configureColumns();
        tblUser.setItems(users);
        tblUser.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldUser, selectedUser) -> {
                    if (selectedUser != null) {
                        populateForm(selectedUser);
                    }
                });
        txtSearchByName.textProperty().addListener((observable, oldText, newText) -> searchUsers());
        loadUsers();
    }

    private void configureColumns() {
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getName()));
        colPassword.setCellValueFactory(cell -> new SimpleStringProperty(mask(cell.getValue().getPassword())));
        colDOB.setCellValueFactory(cell -> new SimpleObjectProperty<>(displayDate(cell.getValue().getDateOfBirth())));
        colGender.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getGender()));
        colEmail.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getEmail()));
        colPhone.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getPhoneNo()));
        colRole.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getRole()));
        colAddress.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getAddress()));
        colJoinDate.setCellValueFactory(cell -> new SimpleObjectProperty<>(displayDate(cell.getValue().getJoinedDate())));
        colImage.setCellValueFactory(cell -> new SimpleStringProperty(storedImageName(cell.getValue().getImage())));
    }

    private void loadUsers() {
        try {
            users.setAll(userService.getUsers());
        } catch (SQLException exception) {
            showError("Could not load users", exception.getMessage());
        }
    }

    private void searchUsers() {
        try {
            users.setAll(userService.searchUsers(txtSearchByName.getText()));
        } catch (SQLException exception) {
            showError("Could not search users", exception.getMessage());
        }
    }

    private void populateForm(User user) {
        txtUserName.setText(user.getName());
        txtPassword.setText(user.getPassword());
        txtEmail.setText(user.getEmail());
        txtPhoneNo.setText(user.getPhoneNo());
        txtAddress.setText(user.getAddress());
        dateDOB.setValue(user.getDateOfBirth());
        dateJoin.setValue(user.getJoinedDate());
        if ("Male".equalsIgnoreCase(user.getGender())) {
            genderGroup.selectToggle(rbtMale);
        } else if ("Female".equalsIgnoreCase(user.getGender())) {
            genderGroup.selectToggle(rbtFemale);
        } else {
            genderGroup.selectToggle(null);
        }
        cbRole.setValue(user.getRole());
        selectedImageFile = null;
        imgUser.setImage(loadImage(user.getImage()));
    }


    @FXML
    private void clickRegister() {
        try {
            User selected = tblUser.getSelectionModel().getSelectedItem();
            User user = formToUser(selected == null ? 0 : selected.getUserId());
            userService.registerUser(user);
            clearForm();
            searchUsers();
            showInformation("User registered.");
        } catch (IllegalArgumentException | SQLException exception) {
            showError("Could not register the user", exception.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        clearForm();
    }

    @FXML
    private void clickSubmitImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a profile picture");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File chosen = chooser.showOpenDialog(imgUser.getScene() == null ? null : imgUser.getScene().getWindow());
        if (chosen == null) {
            return;
        }
        try {
            selectedImageFile = copyImage(chosen);
            imgUser.setImage(new Image(selectedImageFile.toURI().toString()));
        } catch (Exception exception) {
            showError("Could not load the image", exception.getMessage());
        }
    }

    private void clearForm() {
        tblUser.getSelectionModel().clearSelection();
        txtUserName.clear();
        txtPassword.clear();
        txtEmail.clear();
        txtPhoneNo.clear();
        txtAddress.clear();
        dateDOB.setValue(null);
        dateJoin.setValue(null);
        genderGroup.selectToggle(null);
        cbRole.setValue(null);
        selectedImageFile = null;
        imgUser.setImage(null);
    }

    private User formToUser(int userId) {
        User selected = tblUser.getSelectionModel().getSelectedItem();
        String image = selectedImageFile == null
                ? storedImageName(selected == null ? null : selected.getImage())
                : selectedImageFile.getName();
        return new User(userId, text(txtUserName), txtPassword.getText(), dateDOB.getValue(), selectedGender(),
                text(txtEmail), text(txtPhoneNo), cbRole.getValue(), text(txtAddress), dateJoin.getValue(), image);
    }

    private String selectedGender() {
        if (rbtMale.isSelected()) {
            return "Male";
        }
        if (rbtFemale.isSelected()) {
            return "Female";
        }
        throw new IllegalArgumentException("Select a gender.");
    }


    private File copyImage(File source) throws java.io.IOException {
        File directory = new File(System.getProperty(IMAGE_DIRECTORY_PROPERTY, "book-images"));
        if (!directory.exists() && !directory.mkdirs()) {
            throw new java.io.IOException("Could not create image directory: " + directory);
        }
        String extension = source.getName().contains(".")
                ? source.getName().substring(source.getName().lastIndexOf('.')) : ".img";
        String safeName = source.getName().replaceAll("[^A-Za-z0-9._-]", "_");
        File destination = new File(directory, safeName + "-" + System.nanoTime() + extension);
        Files.copy(source.toPath(), destination.toPath());
        return destination;
    }

    private Image loadImage(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            return null;
        }
        File directory = new File(System.getProperty(IMAGE_DIRECTORY_PROPERTY, "book-images"));
        File directoryFile = new File(directory, storedPath);
        File directFile = new File(storedPath);
        File file = directoryFile.isFile() ? directoryFile : directFile;
        return file.isFile() ? new Image(file.toURI().toString()) : null;
    }

    private static String storedImageName(String storedPath) {
        return storedPath == null || storedPath.isBlank() ? storedPath : new File(storedPath).getName();
    }

    private static String mask(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        return "*".repeat(password.length());
    }

    private static String text(TextField field) {
        return field.getText().trim();
    }

    private static String text(TextArea field) {
        return field.getText().trim();
    }

    private static String displayDate(LocalDate date) {
        return date == null ? "" : date.format(DISPLAY_DATE);
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("User management");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String message, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("User management");
        alert.setHeaderText(null);
        if (detail != null) {
            alert.setContentText(detail);
        }
        alert.showAndWait();
    }
}

