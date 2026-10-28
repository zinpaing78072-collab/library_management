package com.example.controller;

import com.example.model.Author;
import com.example.model.Book;
import com.example.model.Category;
import com.example.model.Publisher;
import com.example.service.BookService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;

/** Controller for the book form component. */
public class BookFormController {
    private static final String IMAGE_DIRECTORY_PROPERTY = "library.management.image.directory";

    @FXML private TextField txtTitle;
    @FXML private TextField txtISBN;
    @FXML private TextField txtPYear;
    @FXML private TextField txtCopies;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<Author> cbAuthor;
    @FXML private ComboBox<Category> cbCategory;
    @FXML private ComboBox<Publisher> cbPublisher;
    @FXML private ImageView imgBook;
    @FXML private Button btnUploadBook;
    @FXML private Button btnSaveBook;
    @FXML private Button btnClear;
    @FXML private Label btnArivial;
    @FXML private TextField txtArrival;

    private final ObservableList<Author> authors = FXCollections.observableArrayList();
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableList<Publisher> publishers = FXCollections.observableArrayList();
    private BookService bookService;
    private BookTableController tableController;
    private File selectedImageFile;
    private Book editingBook;

    public void setSession(BookSession session) {
        this.bookService = session.bookService();
        session.registerFormController(this);
    }

    public void setTableController(BookTableController tableController) {
        this.tableController = tableController;
    }

    @FXML
    private void initialize() {
        btnUploadBook.setOnAction(event -> clickUploadBook());
        btnSaveBook.setOnAction(event -> clickSaveBook());
        btnClear.setOnAction(event -> clickClear());
        btnArivial.setVisible(false);
        btnArivial.setManaged(false);
        txtArrival.setVisible(false);
        txtArrival.setManaged(false);
        loadReferenceData();
    }

    public void populateForm(Book book) {
        this.editingBook = book;
        txtTitle.setText(book.getTitle());
        txtISBN.setText(book.getIsbn());
        txtPYear.setText(Integer.toString(book.getPublishedYear()));
        txtCopies.setText(Integer.toString(book.getCopies()));
        txtDescription.setText(book.getDescription());
        cbAuthor.setValue(findById(authors, book.getAuthorId()));
        cbCategory.setValue(findById(categories, book.getCategoryId()));
        cbPublisher.setValue(findById(publishers, book.getPublisherId()));
        selectedImageFile = null;
        imgBook.setImage(loadImage(book.getImage()));
        // Switch to edit mode: show arrival copies field and relabel buttons
        btnArivial.setVisible(true);
        btnArivial.setManaged(true);
        txtArrival.setVisible(true);
        txtArrival.setManaged(true);
        txtArrival.setText("");
        btnSaveBook.setText("Update");
        btnClear.setText("Delete");
    }

    public void clearForm() {
        this.editingBook = null;
        if (tableController != null) {
            tableController.clearSelection();
        }
        txtTitle.clear();
        txtISBN.clear();
        txtPYear.clear();
        txtCopies.clear();
        txtDescription.clear();
        cbAuthor.setValue(null);
        cbCategory.setValue(null);
        cbPublisher.setValue(null);
        selectedImageFile = null;
        imgBook.setImage(null);
        // Return to save mode: hide arrival copies field and reset buttons
        btnArivial.setVisible(false);
        btnArivial.setManaged(false);
        txtArrival.setVisible(false);
        txtArrival.setManaged(false);
        btnSaveBook.setText("Save");
        btnClear.setText("Clear");
    }

    @FXML
    private void clickSaveBook() {
        try {
            Book selected = tableController == null ? null : tableController.selectedBook();
            if (editingBook != null && selected != null) {
                // Update mode: add arrival copies to existing copies
                int arrival = integer(txtArrival, "Arrival copies");
                if (arrival < 0) {
                    throw new IllegalArgumentException("Arrival copies cannot be negative.");
                }
                Book book = formToBook(selected.getBookId());
                book.setCopies(selected.getCopies() + arrival);
                bookService.updateBook(book);
                if (tableController != null) {
                    tableController.loadBooks();
                }
                clearForm();
                showInformation("Book updated.");
            } else {
                // Save mode: create a new book
                Book book = formToBook(0);
                bookService.saveBook(book);
                if (tableController != null) {
                    tableController.loadBooks();
                }
                clearForm();
                showInformation("Book saved.");
            }
        } catch (IllegalArgumentException | SQLException exception) {
            showError("Could not save the book", exception.getMessage());
        }
    }

    @FXML
    private void clickClear() {
        Book selected = tableController == null ? null : tableController.selectedBook();
        if (editingBook != null && selected != null) {
            // Delete mode
            try {
                bookService.deleteBook(selected.getBookId());
                if (tableController != null) {
                    tableController.loadBooks();
                }
                clearForm();
                showInformation("Book deleted.");
            } catch (SQLException exception) {
                showError("Could not delete the book", exception.getMessage());
            }
        } else {
            clearForm();
        }
    }

    @FXML
    private void clickUploadBook() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a book cover");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File chosen = chooser.showOpenDialog(imgBook.getScene() == null ? null : imgBook.getScene().getWindow());
        if (chosen == null) {
            return;
        }
        try {
            selectedImageFile = copyImage(chosen);
            imgBook.setImage(new Image(selectedImageFile.toURI().toString()));
        } catch (Exception exception) {
            showError("Could not load the image", exception.getMessage());
        }
    }

    private void loadReferenceData() {
        try {
            authors.setAll(bookService.getAuthors());
            categories.setAll(bookService.getCategories());
            publishers.setAll(bookService.getPublishers());
            cbAuthor.setItems(authors);
            cbCategory.setItems(categories);
            cbPublisher.setItems(publishers);
        } catch (SQLException exception) {
            showError("Could not load book form data", exception.getMessage());
        }
    }

    private Book formToBook(int bookId) {
        Book selected = tableController == null ? null : tableController.selectedBook();
        String image = selectedImageFile == null
                ? storedImageName(selected == null ? null : selected.getImage())
                : selectedImageFile.getName();
        return new Book(bookId, text(txtTitle), text(txtISBN), selectedId(cbAuthor), selectedId(cbPublisher),
                integer(txtPYear, "Published year"), selectedId(cbCategory), text(txtDescription),
                integer(txtCopies, "Copies"), image);
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

    private static int selectedId(ComboBox<?> comboBox) {
        Object value = comboBox.getValue();
        if (value instanceof Author author) {
            return author.getAuthorId();
        }
        if (value instanceof Category category) {
            return category.getCategoryId();
        }
        if (value instanceof Publisher publisher) {
            return publisher.getPublisherId();
        }
        throw new IllegalArgumentException("Select a value from the list.");
    }

    private static <T> T findById(List<T> values, int id) {
        return values.stream().filter(value -> {
            if (value instanceof Author author) {
                return author.getAuthorId() == id;
            }
            if (value instanceof Category category) {
                return category.getCategoryId() == id;
            }
            if (value instanceof Publisher publisher) {
                return publisher.getPublisherId() == id;
            }
            return false;
        }).findFirst().orElse(null);
    }

    private static int integer(TextField field, String label) {
        try {
            return Integer.parseInt(field.getText().trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(label + " must be a number.");
        }
    }

    private static String text(TextField field) {
        return field.getText().trim();
    }

    private static String text(TextArea field) {
        return field.getText().trim();
    }

    private void showInformation(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setTitle("Book management");
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String message, String detail) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message);
        alert.setTitle("Book management");
        alert.setHeaderText(null);
        if (detail != null) {
            alert.setContentText(detail);
        }
        alert.showAndWait();
    }
}


