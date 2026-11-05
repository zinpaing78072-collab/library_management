package com.example;

import com.example.controller.MemberController;
import com.example.service.MemberService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class MemberApp {

    private MemberApp() {
    }

    /**
     * Starts JavaFX. The outer class deliberately does not extend
     * {@link Application}: a class that does cannot be started on a plain
     * class path (the JDK reports "JavaFX runtime components are missing").
     */
    public static void main(String[] args) {
        Application.launch(Screen.class, args);
    }

    /** The JavaFX application: the members screen on its own. */
    public static final class Screen extends Application {

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
    }
}
