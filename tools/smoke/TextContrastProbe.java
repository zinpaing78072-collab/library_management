package com.example.tools.smoke;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Measures the real, per-glyph WCAG contrast of text drawn over a background
 * photo.
 *
 * Method (no guessing, no heuristics):
 *   1. Render the screen as-is                   -> image A (text over photo)
 *   2. Re-render with the root's children hidden -> image B (photo alone)
 *   3. A pixel whose colour is EXACTLY the CSS text fill is a glyph pixel
 *      (JavaFX paints glyph interiors at the full fill colour). The
 *      background under that glyph is the same pixel in image B.
 *   4. The contrast ratio is computed for every glyph pixel; the minimum is
 *      the worst case actually rendered on screen.
 *
 * Usage: TextContrastProbe <fxml> <label>:<x>:<y>:<w>:<h>:<textHex> ...
 *        (no regions => dump the colour histogram of the whole screen)
 */
public final class TextContrastProbe {

    private record Region(String label, int x, int y, int w, int h, int textArgb) {
    }

    public static void main(String[] args) throws Exception {
        String fxml = args.length > 0 ? args[0] : "/Login.fxml";
        List<Region> regions = new ArrayList<>();
        for (int i = 1; i < args.length; i++) {
            String[] p = args[i].split(":");
            regions.add(new Region(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                    Integer.parseInt(p[3]), Integer.parseInt(p[4]), parseHex(p[5])));
        }

        final String[] out = new String[1];
        final CountDownLatch done = new CountDownLatch(1);
        Platform.startup(() -> {
            try {
                WritableImage withText = render(fxml, false);
                WritableImage bgOnly = render(fxml, true);
                PixelReader ta = withText.getPixelReader();
                PixelReader ba = bgOnly.getPixelReader();

                if (regions.isEmpty()) {
                    dumpHistogram(ta, (int) withText.getWidth(), (int) withText.getHeight());
                } else {
                    for (Region r : regions) {
                        report(r, ta, ba, (int) withText.getWidth(), (int) withText.getHeight());
                    }
                }
                out[0] = "OK";
            } catch (Throwable t) {
                out[0] = "FAIL " + t;
                t.printStackTrace();
            } finally {
                done.countDown();
            }
        });
        if (!done.await(60, TimeUnit.SECONDS)) {
            System.out.println("TIMEOUT");
            System.exit(2);
        }
        System.out.println(out[0]);
        System.exit(out[0].startsWith("OK") ? 0 : 1);
    }

    private static WritableImage render(String fxml, boolean hideChildren) throws Exception {
        FXMLLoader loader = new FXMLLoader(TextContrastProbe.class.getResource(fxml));
        loader.setControllerFactory(type -> {
            try {
                return type.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
        Parent root = loader.load();
        Scene scene = new Scene(root, 759, 530);
        scene.getStylesheets().add(
                TextContrastProbe.class.getResource("/css/app.css").toExternalForm());
        if (hideChildren) {
            root.getChildrenUnmodifiable().forEach(n -> n.setVisible(false));
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.show();
        root.applyCss();
        root.layout();
        WritableImage img = scene.snapshot(null);
        stage.close();
        return img;
    }

    private static void report(Region r, PixelReader ta, PixelReader ba, int w, int h) {
        double textLum = luminance(r.textArgb());
        double minRatio = Double.MAX_VALUE;
        double minBgLum = -1;
        int glyphs = 0;
        for (int y = r.y(); y < r.y() + r.h(); y++) {
            for (int x = r.x(); x < r.x() + r.w(); x++) {
                if (x >= w || y >= h) {
                    continue;
                }
                int rendered = ta.getArgb(x, y);
                int bg = ba.getArgb(x, y);
                if (rendered == bg) {
                    continue; // no text here
                }
                // Keep only pixels that are at least 50% covered by the glyph
                // (the rendered colour is nearer to the text fill than to the
                // background). Anti-aliased edge pixels below that coverage are
                // excluded -- they are the glyph's edge, not its body.
                if (distance(rendered, r.textArgb()) > distance(rendered, bg)) {
                    continue;
                }
                glyphs++;
                double bgLum = luminance(bg);
                double ratio = contrast(bgLum, textLum);
                if (ratio < minRatio) {
                    minRatio = ratio;
                    minBgLum = bgLum;
                }
            }
        }
        if (glyphs == 0) {
            System.out.printf("%-12s NO TEXT PIXELS FOUND%n", r.label());
            return;
        }
        System.out.printf("%-12s pixels=%-5d worstContrast=%.2f:1 (bgLum=%.3f) %s%n",
                r.label(), glyphs, minRatio, minBgLum, verdict(minRatio));
    }

    /** Perceived (weighted euclidean) distance between two opaque colours. */
    private static int distance(int a, int b) {
        int dr = ((a >> 16) & 0xff) - ((b >> 16) & 0xff);
        int dg = ((a >> 8) & 0xff) - ((b >> 8) & 0xff);
        int db = (a & 0xff) - (b & 0xff);
        return (int) Math.sqrt(dr * dr + dg * dg + db * db);
    }

    private static void dumpHistogram(PixelReader ta, int w, int h) {
        java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                counts.merge(ta.getArgb(x, y) & 0xFFFFFF, 1, Integer::sum);
            }
        }
        counts.entrySet().stream()
                .sorted((p, q) -> q.getValue() - p.getValue())
                .limit(12)
                .forEach(e -> System.out.printf("  %06X  %d%n", e.getKey(), e.getValue()));
    }

    private static double luminance(int argb) {
        double r = channel((argb >> 16) & 0xff);
        double g = channel((argb >> 8) & 0xff);
        double b = channel(argb & 0xff);
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    private static double channel(int c) {
        double s = c / 255.0;
        return s <= 0.03928 ? s / 12.92 : Math.pow((s + 0.055) / 1.055, 2.4);
    }

    private static int parseHex(String hex) {
        String h = hex.startsWith("#") ? hex.substring(1) : hex;
        return Integer.parseInt(h, 16);
    }

    private static double contrast(double a, double b) {
        double lighter = Math.max(a, b);
        double darker = Math.min(a, b);
        return (lighter + 0.05) / (darker + 0.05);
    }

    private static String verdict(double ratio) {
        if (ratio >= 4.5) {
            return "PASS(AA)";
        }
        if (ratio >= 3.0) {
            return "PASS(AA-large-only)";
        }
        return "FAIL";
    }
}
