package com.example;

import com.example.controller.AdminController;
import com.example.controller.AdminDashboardController;
import com.example.controller.IssueBookController;
import com.example.controller.ReturnBookController;
import com.example.service.BookTransactionService;
import com.example.controller.BookController;
import com.example.controller.BookFormController;
import com.example.controller.BookSession;
import com.example.controller.BookTableController;
import com.example.controller.AuthorController;
import com.example.controller.CategoryController;
import com.example.controller.LibrarianController;
import com.example.controller.LoginController;
import com.example.controller.MemberController;
import com.example.controller.PublisherController;
import com.example.controller.UserController;
import com.example.model.User;
import com.example.service.AuthorService;
import com.example.service.BookService;
import com.example.service.CategoryService;
import com.example.service.LoginService;
import com.example.service.MemberService;
import com.example.service.PublisherService;
import com.example.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

/** Entry point: login screen, then the screen matching the signed-in role. */
public class LoginApp extends Application {

    private static final String CSS = "/css/app.css";

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginApp.class.getResource("/Login.fxml"));
        loader.setControllerFactory(controllerType -> {
            if (controllerType == LoginController.class) {
                LoginController controller = new LoginController();
                controller.setLoginService(new LoginService());
                controller.setOnLoginSuccess(user -> openRoleScreen(stage, user));
                return controller;
            }
            throw new IllegalArgumentException("Unsupported FXML controller: " + controllerType.getName());
        });
        Parent root = loader.load();
        stage.setTitle("Library Management - Login");
        stage.setScene(newScene(root));
        stage.show();
    }

    /** Admin lands on the admin dashboard, Librarian on the librarian dashboard. */
    private void openRoleScreen(Stage stage, User user) {
        try {
            if ("admin".equalsIgnoreCase(user.getRole())) {
                openAdmin(stage, user);
            } else {
                openLibrarian(stage, user);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not open the screen for role " + user.getRole(), exception);
        }
    }

    /** Opens the admin dashboard (Admin.fxml). */
    private void openAdmin(Stage stage, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginApp.class.getResource("/Admin.fxml"));
        loader.setControllerFactory(controllerType -> {
            if (controllerType == AdminController.class) {
                AdminController controller = new AdminController();
                controller.setUser(user);
                controller.setScreenLoader(path -> loadIntoPane(controller.getAnchor(), path));
                controller.setLogoutHandler(() -> showLogin(stage));
                return controller;
            }
            throw new IllegalArgumentException("Unsupported FXML controller: " + controllerType.getName());
        });
        apply(stage, "Library Management - Admin", loader.load());
    }

    /** Reloads the login screen (used by LogOut on the librarian/admin dashboard). */
    private void showLogin(Stage stage) {
        try {
            start(stage);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not reopen the login screen", exception);
        }
    }

    private void openLibrarian(Stage stage, User user) throws IOException {
        FXMLLoader loader = new FXMLLoader(LoginApp.class.getResource("/Librarian.fxml"));
        loader.setControllerFactory(controllerType -> {
            if (controllerType == LibrarianController.class) {
                LibrarianController controller = new LibrarianController();
                controller.setUser(user);
                controller.setScreenLoader(path -> loadIntoPane(controller.getLibrarianPane(), path));
                controller.setLogoutHandler(() -> showLogin(stage));
                return controller;
            }
            throw new IllegalArgumentException("Unsupported FXML controller: " + controllerType.getName());
        });
        apply(stage, "Library Management - Librarian", loader.load());
    }

    /** Loads a nested screen (e.g. /Book.fxml) into the given content pane. */
    private void loadIntoPane(AnchorPane pane, String fxmlPath) {
        try {
            FXMLLoader nested = new FXMLLoader(LoginApp.class.getResource(fxmlPath));
            BookSession[] session = new BookSession[1];
            nested.setControllerFactory(controllerType -> createNestedController(controllerType,
                    () -> {
                        if (session[0] == null) {
                            session[0] = new BookSession(new BookService());
                        }
                        return session[0];
                    }));
            Parent screen = nested.load();
            unbindPrevious(pane);
            pane.getChildren().setAll(screen);
            fitToPane((javafx.scene.layout.Region) screen, pane);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not open " + fxmlPath, exception);
        }
    }

    /** Unbinds the fit bindings of the screen currently occupying the pane so the
     *  listeners do not pile up on the pane's size properties across navigations. */
    private void unbindPrevious(AnchorPane pane) {
        for (javafx.scene.Node child : pane.getChildren()) {
            if (child instanceof javafx.scene.layout.Region region) {
                region.scaleXProperty().unbind();
                region.scaleYProperty().unbind();
                region.translateXProperty().unbind();
                region.translateYProperty().unbind();
            }
        }
    }

    /** Positions a nested screen at the top-left of the content pane at its natural
     *  size (1:1 scale). Scaling via scaleX/scaleY is intentionally avoided because
     *  it distorts fixed-layout elements such as TableView columns, causing text
     *  truncation and blurred rendering. If the screen is larger than the pane it
     *  will overflow — AnchorPane does not clip children by default. */
    private void fitToPane(javafx.scene.layout.Region screen, AnchorPane pane) {
        screen.scaleXProperty().unbind();
        screen.scaleYProperty().unbind();
        screen.translateXProperty().unbind();
        screen.translateYProperty().unbind();
        screen.setScaleX(1.0);
        screen.setScaleY(1.0);
        screen.setTranslateX(0.0);
        screen.setTranslateY(0.0);
    }

    private Object createNestedController(Class<?> controllerType,
                                          java.util.function.Supplier<BookSession> sessions) {
        if (controllerType == BookController.class) {
            BookController controller = new BookController();
            controller.setSession(sessions.get());
            return controller;
        }
        if (controllerType == BookTableController.class) {
            BookTableController controller = new BookTableController();
            controller.setSession(sessions.get());
            return controller;
        }
        if (controllerType == IssueBookController.class) {
            IssueBookController controller = new IssueBookController();
            controller.setTransactionService(new BookTransactionService());
            return controller;
        }
        if (controllerType == ReturnBookController.class) {
            ReturnBookController controller = new ReturnBookController();
            controller.setTransactionService(new BookTransactionService());
            return controller;
        }
        if (controllerType == BookFormController.class) {
            BookFormController controller = new BookFormController();
            controller.setSession(sessions.get());
            return controller;
        }
        if (controllerType == UserController.class) {
            UserController controller = new UserController();
            controller.setUserService(new UserService());
            return controller;
        }
        if (controllerType == MemberController.class) {
            MemberController controller = new MemberController();
            controller.setMemberService(new MemberService());
            return controller;
        }
        if (controllerType == CategoryController.class) {
            CategoryController controller = new CategoryController();
            controller.setCategoryService(new CategoryService());
            return controller;
        }
        if (controllerType == AuthorController.class) {
            AuthorController controller = new AuthorController();
            controller.setAuthorService(new AuthorService());
            return controller;
        }
        if (controllerType == PublisherController.class) {
            PublisherController controller = new PublisherController();
            controller.setPublisherService(new PublisherService());
            return controller;
        }
        if (controllerType == AdminDashboardController.class) {
            AdminDashboardController controller = new AdminDashboardController();
            controller.setBookService(new BookService());
            controller.setTransactionService(new BookTransactionService());
            return controller;
        }
        throw new IllegalArgumentException("Unsupported FXML controller: " + controllerType.getName());
    }

    private void apply(Stage stage, String title, Parent root) {
        stage.setTitle(title);
        stage.setScene(newScene(root));
    }

    private static Scene newScene(Parent root) {
        Scene scene = new Scene(root);
        scene.getStylesheets().add(LoginApp.class.getResource(CSS).toExternalForm());
        return scene;
    }



    public static void main(String[] args) {
        launch(args);
    }
}
