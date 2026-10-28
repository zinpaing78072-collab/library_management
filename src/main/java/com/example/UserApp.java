package com.example;

import com.example.controller.UserController;
import com.example.service.UserService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class UserApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(UserApp.class.getResource("/User.fxml"));
        loader.setControllerFactory(controllerType -> {
            if (controllerType == UserController.class) {
                UserController controller = new UserController();
                controller.setUserService(new UserService());
                return controller;
            }
            throw new IllegalArgumentException("Unsupported FXML controller: " + controllerType.getName());
        });
        Parent root = loader.load();
        stage.setTitle("Library Management - Users");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(UserApp.class.getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
