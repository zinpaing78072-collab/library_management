package com.example;

import com.example.controller.MemberController;
import com.example.service.MemberService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MemberApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(MemberApp.class.getResource("/Member.fxml"));
        loader.setControllerFactory(controllerType -> {
            if (controllerType == MemberController.class) {
                MemberController controller = new MemberController();
                controller.setMemberService(new MemberService());
                return controller;
            }
            throw new IllegalArgumentException("Unsupported FXML controller: " + controllerType.getName());
        });
        Parent root = loader.load();
        stage.setTitle("Library Management - Members");
        Scene scene = new Scene(root);
        scene.getStylesheets().add(MemberApp.class.getResource("/css/app.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
