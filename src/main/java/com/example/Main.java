package com.example;

import com.example.controller.BookController;
import com.example.controller.BookFormController;
import com.example.controller.BookSession;
import com.example.controller.BookTableController;
import com.example.service.BookService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Book.fxml"));
        BookSession session = new BookSession(new BookService());
        loader.setControllerFactory(controllerType -> {
            if (controllerType == BookController.class) {
                BookController controller = new BookController();
                controller.setSession(session);
                return controller;
            }
            if (controllerType == BookTableController.class) {
                BookTableController controller = new BookTableController();
                controller.setSession(session);
                return controller;
            }
            if (controllerType == BookFormController.class) {
                BookFormController controller = new BookFormController();
                controller.setSession(session);
                return controller;
            }
            throw new IllegalArgumentException("Unsupported FXML controller: " + controllerType.getName());
        });
        Parent root = loader.load();
        stage.setTitle("Library Management - Books");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(Main.class.getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}