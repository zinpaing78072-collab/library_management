package com.example.tools.smoke;

import com.example.controller.CategoryController;
import com.example.controller.LibrarianController;
import com.example.repository.CategoryRepository;
import com.example.service.CategoryService;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

/** Samples pixels of the librarian dashboard with a nested screen loaded in the
 *  content pane, to see whether the nested screen paints its own background. */
public final class NestedProbe {

    public static void main(String[] args) throws Exception {
        final String[] out = new String[1];
        Platform.startup(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(NestedProbe.class.getResource("/Librarian.fxml"));
                loader.setControllerFactory(NestedProbe::controller);
                Parent root = loader.load();
                LibrarianController lib = (LibrarianController) loader.getController();

                FXMLLoader nested = new FXMLLoader(NestedProbe.class.getResource("/Category.fxml"));
                nested.setControllerFactory(NestedProbe::controller);
                Parent nestedRoot = nested.load();
                lib.getLibrarianPane().getChildren().setAll(nestedRoot);

                Scene scene = new Scene(root, 1256, 752);
                scene.getStylesheets().add(NestedProbe.class.getResource("/css/app.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                root.applyCss();
                root.layout();

                Image image = scene.snapshot(null);
                PixelReader reader = image.getPixelReader();
                for (int[] p : new int[][]{{700, 400}, {200, 400}, {1200, 700}, {300, 120}}) {
                    int argb = reader.getArgb(p[0], p[1]);
                    System.out.printf("pixel %d,%d = #%06X%n", p[0], p[1], argb & 0xFFFFFF);
                }
                out[0] = "OK";
            } catch (Throwable t) {
                out[0] = "FAIL " + t;
            }
        });
        Thread.sleep(6000);
        System.out.println(out[0] == null ? "TIMEOUT" : out[0]);
        System.exit(0);
    }

    private static Object controller(Class<?> type) {
        if (type == LibrarianController.class) {
            LibrarianController c = new LibrarianController();
            c.setScreenLoader(p -> { });
            c.setLogoutHandler(() -> { });
            return c;
        }
        if (type == CategoryController.class) {
            CategoryController c = new CategoryController();
            c.setCategoryService(new CategoryService(stub(CategoryRepository.class)));
            return c;
        }
        throw new IllegalArgumentException("Unsupported " + type);
    }

    @SuppressWarnings("unchecked")
    private static <T> T stub(Class<T> repositoryType) {
        InvocationHandler handler = (proxy, method, args) -> {
            Class<?> rt = method.getReturnType();
            if (rt == List.class) return List.of();
            if (rt == long.class) return 0L;
            if (rt == boolean.class) return false;
            if (rt == int.class) return 0;
            if (java.util.Optional.class.isAssignableFrom(rt)) return java.util.Optional.empty();
            if (rt == void.class) return null;
            if (rt == String.class) return "";
            return null;
        };
        return (T) Proxy.newProxyInstance(NestedProbe.class.getClassLoader(),
                new Class<?>[]{repositoryType}, handler);
    }
}
