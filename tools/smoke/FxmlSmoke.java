package com.example.tools.smoke;

import com.example.controller.AuthorController;
import com.example.controller.CategoryController;
import com.example.controller.IssueBookController;
import com.example.controller.MemberController;
import com.example.controller.PublisherController;
import com.example.controller.ReturnBookController;
import com.example.controller.BookController;
import com.example.controller.BookFormController;
import com.example.controller.BookSession;
import com.example.controller.BookTableController;
import com.example.controller.LibrarianController;
import com.example.controller.LoginController;
import com.example.controller.UserController;
import com.example.model.User;
import com.example.repository.AuthorRepository;
import com.example.repository.BookRepository;
import com.example.repository.BookTransactionRepository;
import com.example.repository.CategoryRepository;
import com.example.repository.MemberRepository;
import com.example.repository.PublisherRepository;
import com.example.repository.UserRepository;
import com.example.service.AuthorService;
import com.example.service.BookService;
import com.example.service.BookTransactionService;
import com.example.service.CategoryService;
import com.example.service.LoginService;
import com.example.service.MemberService;
import com.example.service.PublisherService;
import com.example.service.UserService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.time.LocalDate;
import java.util.List;

/**
 * Headless FXML smoke test. Loads every screen with in-memory stub repositories
 * (no MySQL needed) and reports the exact failure per screen.
 */
public final class FxmlSmoke {

    private static int failures = 0;

    public static void main(String[] args) throws Exception {
        Platform.startup(() -> {
        });
        String[] screens = {"/Login.fxml", "/User.fxml", "/Librarian.fxml", "/Category.fxml",
                "/Author.fxml", "/Publisher.fxml", "/Book.fxml", "/Member.fxml", "/IssueBook.fxml",
                "/ReturnBook.fxml"};
        for (String screen : screens) {
            load(screen);
        }
        buttonWiring();
        dashboardNavigation();
        stageLayout();
        System.out.println(failures == 0 ? "ALL SCREENS LOADED" : failures + " CHECK(S) FAILED");
        System.exit(failures == 0 ? 0 : 1);
    }

    private static void load(String path) {
        try {
            FXMLLoader loader = new FXMLLoader(FxmlSmoke.class.getResource(path));
            loader.setControllerFactory(FxmlSmoke::createController);
            Parent root = loader.load();
            System.out.println("PASS  " + path + "  root=" + root.getClass().getSimpleName());
            for (String key : List.of("bookTableController", "bookFormController")) {
                if (loader.getNamespace().containsKey(key)) {
                    Object value = loader.getNamespace().get(key);
                    System.out.println("      ns[" + key + "] = "
                            + (value == null ? "null" : value.getClass().getName()));
                }
            }
        } catch (Throwable t) {
            failures++;
            System.out.println("FAIL  " + path);
            Throwable cause = t;
            while (cause != null) {
                System.out.println("      " + cause.getClass().getName() + ": " + cause.getMessage());
                cause = cause.getCause();
            }
        }
    }

    /**
     * Fails if a Button is neither wired through the FXML onAction attribute nor
     * through a programmatic setOnAction call. Such a button is dead in the UI.
     */
    private static void buttonWiring() {
        String[] screens = {"/Login.fxml", "/User.fxml", "/Librarian.fxml", "/Category.fxml",
                "/Author.fxml", "/Publisher.fxml", "/Book.fxml", "/Member.fxml", "/IssueBook.fxml",
                "/ReturnBook.fxml"};
        for (String path : screens) {
            try {
                FXMLLoader loader = new FXMLLoader(FxmlSmoke.class.getResource(path));
                loader.setControllerFactory(FxmlSmoke::createController);
                Parent root = loader.load();
                List<String> dead = new java.util.ArrayList<>();
                collectDeadButtons(root, dead);
                if (dead.isEmpty()) {
                    System.out.println("PASS  button wiring " + path);
                } else {
                    failures++;
                    System.out.println("FAIL  button wiring " + path + " -> unwired: " + dead);
                }
            } catch (Throwable t) {
                failures++;
                System.out.println("FAIL  button wiring " + path);
                Throwable cause = t;
                while (cause != null) {
                    System.out.println("      " + cause.getClass().getName() + ": " + cause.getMessage());
                    cause = cause.getCause();
                }
            }
        }
    }

    /** Walks the scene graph collecting buttons that have no action handler. */
    private static void collectDeadButtons(javafx.scene.Node node, List<String> dead) {
        if (node instanceof javafx.scene.control.Button button) {
            if (button.getOnAction() == null) {
                dead.add(button.getId() == null ? button.getText() : button.getId());
            }
        }
        if (node instanceof javafx.scene.control.TabPane tabPane) {
            for (javafx.scene.control.Tab tab : tabPane.getTabs()) {
                collectDeadButtons(tab.getContent(), dead);
            }
        }
        if (node instanceof javafx.scene.control.SplitPane splitPane) {
            for (javafx.scene.Node item : splitPane.getItems()) {
                collectDeadButtons(item, dead);
            }
        }
        if (node instanceof javafx.scene.Parent parent) {
            for (javafx.scene.Node child : parent.getChildrenUnmodifiable()) {
                collectDeadButtons(child, dead);
            }
        }
    }

    /**
     * Loads every screen into a live Scene and pulses the layout, which surfaces
     * layout-time exceptions that only appear once a node is in a Scene.
     */
    private static void stageLayout() {
        javafx.scene.Scene scene = new javafx.scene.Scene(new javafx.scene.layout.AnchorPane());
        for (String path : List.of("/Login.fxml", "/User.fxml", "/Librarian.fxml", "/Category.fxml",
                "/Author.fxml", "/Publisher.fxml", "/Book.fxml", "/Member.fxml", "/IssueBook.fxml",
                "/ReturnBook.fxml")) {
            try {
                FXMLLoader loader = new FXMLLoader(FxmlSmoke.class.getResource(path));
                loader.setControllerFactory(FxmlSmoke::createController);
                Parent root = loader.load();
                scene.setRoot(root);
                root.applyCss();
                root.layout();
                System.out.println("PASS  scene layout " + path);
            } catch (Throwable t) {
                failures++;
                System.out.println("FAIL  scene layout " + path);
                Throwable cause = t;
                while (cause != null) {
                    System.out.println("      " + cause.getClass().getName() + ": " + cause.getMessage());
                    cause = cause.getCause();
                }
            }
        }
    }

    /** Loads the dashboard and opens every nested screen the sidebar can open. */
    private static void dashboardNavigation() {
        FXMLLoader[] dashboardLoader = new FXMLLoader[1];
        try {
            FXMLLoader loader = new FXMLLoader(FxmlSmoke.class.getResource("/Librarian.fxml"));
            loader.setControllerFactory(type -> {
                if (type == LibrarianController.class) {
                    LibrarianController controller = new LibrarianController();
                    controller.setUser(testUser());
                    controller.setScreenLoader(path -> loadIntoPane(dashboardLoader[0], path));
                    controller.setLogoutHandler(() -> {
                    });
                    return controller;
                }
                throw new IllegalArgumentException("Unsupported FXML controller: " + type.getName());
            });
            dashboardLoader[0] = loader;
            loader.load();
            System.out.println("PASS  /Librarian.fxml loaded as dashboard");
            AnchorPane pane = loader.<LibrarianController>getController().getLibrarianPane();
            for (String path : List.of("/Category.fxml", "/Author.fxml", "/Publisher.fxml", "/Book.fxml",
                    "/Member.fxml", "/IssueBook.fxml", "/ReturnBook.fxml")) {
                loadIntoPane(dashboardLoader[0], path);
                if (pane.getChildren().isEmpty()) {
                    failures++;
                    System.out.println("FAIL  dashboard navigation to " + path + " left the pane empty");
                } else {
                    System.out.println("PASS  dashboard navigation to " + path + " -> "
                            + pane.getChildren().get(0).getClass().getSimpleName());
                }
            }
        } catch (Throwable t) {
            failures++;
            System.out.println("FAIL  dashboard navigation");
            Throwable cause = t;
            while (cause != null) {
                System.out.println("      " + cause.getClass().getName() + ": " + cause.getMessage());
                cause = cause.getCause();
            }
        }
    }

    /** Mirrors LoginApp.loadIntoPane: load a screen into the dashboard content pane. */
    private static void loadIntoPane(FXMLLoader dashboardLoader, String fxmlPath) {
        try {
            FXMLLoader nested = new FXMLLoader(FxmlSmoke.class.getResource(fxmlPath));
            nested.setControllerFactory(FxmlSmoke::createController);
            Parent screen = nested.load();
            AnchorPane pane = dashboardLoader.<LibrarianController>getController().getLibrarianPane();
            pane.getChildren().setAll(screen);
        } catch (java.io.IOException exception) {
            throw new IllegalStateException("Could not open " + fxmlPath, exception);
        }
    }

    private static User testUser() {
        return new User(1, "Test", "pw", LocalDate.of(1990, 1, 1), "Male",
                "t@t.com", "09", "Librarian", "addr", LocalDate.of(2020, 1, 1), null);
    }

    /** Builds a controller with stub services so no MySQL connection is needed. */
    private static Object createController(Class<?> type) {
        if (type == LoginController.class) {
            LoginController controller = new LoginController();
            controller.setLoginService(new LoginService(stub(UserRepository.class)));
            return controller;
        }
        if (type == UserController.class) {
            UserController controller = new UserController();
            controller.setUserService(new UserService(stub(UserRepository.class)));
            return controller;
        }
        if (type == LibrarianController.class) {
            LibrarianController controller = new LibrarianController();
            controller.setUser(new User(1, "Test", "pw", LocalDate.of(1990, 1, 1), "Male",
                    "t@t.com", "09", "Librarian", "addr", LocalDate.of(2020, 1, 1), null));
            controller.setScreenLoader(path -> {
            });
            controller.setLogoutHandler(() -> {
            });
            return controller;
        }
        if (type == CategoryController.class) {
            CategoryController controller = new CategoryController();
            controller.setCategoryService(new CategoryService(stub(CategoryRepository.class)));
            return controller;
        }
        if (type == AuthorController.class) {
            AuthorController controller = new AuthorController();
            controller.setAuthorService(new AuthorService(stub(AuthorRepository.class)));
            return controller;
        }
        if (type == PublisherController.class) {
            PublisherController controller = new PublisherController();
            controller.setPublisherService(new PublisherService(stub(PublisherRepository.class)));
            return controller;
        }
        if (type == MemberController.class) {
            MemberController controller = new MemberController();
            controller.setMemberService(new MemberService(stub(MemberRepository.class)));
            return controller;
        }
        if (type == IssueBookController.class) {
            IssueBookController controller = new IssueBookController();
            controller.setTransactionService(new BookTransactionService(stub(BookTransactionRepository.class)));
            return controller;
        }
        if (type == ReturnBookController.class) {
            ReturnBookController controller = new ReturnBookController();
            controller.setTransactionService(new BookTransactionService(stub(BookTransactionRepository.class)));
            return controller;
        }
        if (type == CategoryController.class) {
            CategoryController controller = new CategoryController();
            controller.setCategoryService(new CategoryService(stub(CategoryRepository.class)));
            return controller;
        }
        if (type == AuthorController.class) {
            AuthorController controller = new AuthorController();
            controller.setAuthorService(new AuthorService(stub(AuthorRepository.class)));
            return controller;
        }
        if (type == PublisherController.class) {
            PublisherController controller = new PublisherController();
            controller.setPublisherService(new PublisherService(stub(PublisherRepository.class)));
            return controller;
        }
        if (type == MemberController.class) {
            MemberController controller = new MemberController();
            controller.setMemberService(new MemberService(stub(MemberRepository.class)));
            return controller;
        }
        if (type == IssueBookController.class) {
            IssueBookController controller = new IssueBookController();
            controller.setTransactionService(new BookTransactionService(stub(BookTransactionRepository.class)));
            return controller;
        }
        if (type == ReturnBookController.class) {
            ReturnBookController controller = new ReturnBookController();
            controller.setTransactionService(new BookTransactionService(stub(BookTransactionRepository.class)));
            return controller;
        }
        if (type == BookController.class) {
            BookController controller = new BookController();
            controller.setSession(bookSession());
            return controller;
        }
        if (type == BookTableController.class) {
            BookTableController controller = new BookTableController();
            controller.setSession(bookSession());
            return controller;
        }
        if (type == BookFormController.class) {
            BookFormController controller = new BookFormController();
            controller.setSession(bookSession());
            return controller;
        }
        throw new IllegalArgumentException("Unsupported FXML controller: " + type.getName());
    }

    private static BookSession sharedSession;

    private static BookSession bookSession() {
        if (sharedSession == null) {
            sharedSession = new BookSession(new BookService(stub(BookRepository.class), stub(AuthorRepository.class),
                    stub(CategoryRepository.class), stub(PublisherRepository.class)));
        }
        return sharedSession;
    }

    /** A repository proxy that returns empty/zero values for every call. */
    @SuppressWarnings("unchecked")
    private static <T> T stub(Class<T> repositoryType) {
        InvocationHandler handler = (proxy, method, args) -> {
            Class<?> returnType = method.getReturnType();
            if (returnType == List.class) {
                return List.of();
            }
            if (returnType == long.class) {
                return 0L;
            }
            if (returnType == boolean.class) {
                return false;
            }
            if (returnType == int.class) {
                return 0;
            }
            if (java.util.Optional.class.isAssignableFrom(returnType)) {
                return java.util.Optional.empty();
            }
            if (returnType == void.class) {
                return null;
            }
            if (returnType == String.class) {
                return "";
            }
            return null;
        };
        return (T) Proxy.newProxyInstance(FxmlSmoke.class.getClassLoader(),
                new Class<?>[]{repositoryType}, handler);
    }
}

