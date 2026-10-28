package com.example.tools.smoke;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Renders a screen to a PNG so the background/veil look can be inspected.
 * Usage: ScreenShot <fxml-path> <output-png> [width] [height]
 */
public final class ScreenShot {

    public static void main(String[] args) throws Exception {
        String fxml = args.length > 0 ? args[0] : "/Login.fxml";
        String out = args.length > 1 ? args[1] : "/tmp/shot.png";
        int width = args.length > 2 ? Integer.parseInt(args[2]) : 759;
        int height = args.length > 3 ? Integer.parseInt(args[3]) : 530;

        final String[] result = new String[1];
        final CountDownLatch done = new CountDownLatch(1);
        Platform.startup(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(ScreenShot.class.getResource(fxml));
                loader.setControllerFactory(type -> {
                    try {
                        return type.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new IllegalStateException(e);
                    }
                });
                Parent root = loader.load();
                Scene scene = new Scene(root, width, height);
                // The theme is attached at the Scene level by the real apps, so the
                // smoke render must do the same or the background never paints.
                scene.getStylesheets().add(
                        ScreenShot.class.getResource("/css/app.css").toExternalForm());
                Stage stage = new Stage();
                stage.setScene(scene);
                stage.show();
                root.applyCss();
                root.layout();

                Image image = scene.snapshot(null);
                PixelReader reader = image.getPixelReader();
                BufferedImage buffered = new BufferedImage(
                        (int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
                for (int y = 0; y < image.getHeight(); y++) {
                    for (int x = 0; x < image.getWidth(); x++) {
                        buffered.setRGB(x, y, reader.getArgb(x, y));
                    }
                }
                ImageIO.write(buffered, "png", new File(out));
                result[0] = "OK " + out;
            } catch (Throwable t) {
                result[0] = "FAIL " + t;
                t.printStackTrace();
            } finally {
                done.countDown();
            }
        });
        if (!done.await(30, TimeUnit.SECONDS)) {
            System.out.println("TIMEOUT waiting for render");
            System.exit(2);
        }
        System.out.println(result[0] == null ? "NO RESULT" : result[0]);
        System.exit(result[0] != null && result[0].startsWith("OK") ? 0 : 1);
    }
}
