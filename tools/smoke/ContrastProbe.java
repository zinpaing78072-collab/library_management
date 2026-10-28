package com.example.tools.smoke;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Computes the WCAG contrast ratio for text drawn over a background image.
 *
 * The screenshot of a screen contains both the text glyphs and the background
 * pixels behind them. The background luminance is estimated as the most common
 * (modal) luminance in the region -- that is the panel/photo the text sits on --
 * while the text luminance is the extreme value that stands out from it.
 *
 * Usage: ContrastProbe <image> <x> <y> <w> <h> <textHex>
 *        ContrastProbe <image> <x> <y> <w> <h>   (auto: darkest vs lightest)
 */
public final class ContrastProbe {

    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File(args[0]));
        int x0 = Integer.parseInt(args[1]);
        int y0 = Integer.parseInt(args[2]);
        int w = Integer.parseInt(args[3]);
        int h = Integer.parseInt(args[4]);

        // Histogram of relative luminance, quantised to 1/1000 steps.
        int[] histogram = new int[1001];
        double sum = 0;
        int n = 0;
        double max = 0;
        double min = 1;
        for (int y = y0; y < y0 + h && y < image.getHeight(); y++) {
            for (int x = x0; x < x0 + w && x < image.getWidth(); x++) {
                double lum = luminance(image.getRGB(x, y));
                histogram[(int) Math.round(lum * 1000)]++;
                sum += lum;
                max = Math.max(max, lum);
                min = Math.min(min, lum);
                n++;
            }
        }
        if (n == 0) {
            System.out.println("EMPTY REGION");
            return;
        }

        // Modal luminance = the background the text is drawn on.
        int modal = 0;
        for (int i = 1; i < histogram.length; i++) {
            if (histogram[i] > histogram[modal]) {
                modal = i;
            }
        }
        double bgLum = modal / 1000.0;

        double textLum;
        String textDesc;
        if (args.length > 5) {
            textLum = luminance(parseHex(args[5]));
            textDesc = "text=" + args[5];
        } else {
            // Auto: the glyphs are the minority luminance, far from the modal one.
            textLum = Math.abs(max - bgLum) > Math.abs(min - bgLum) ? max : min;
            textDesc = String.format("text(auto)=%.3f", textLum);
        }

        double ratio = contrastRatio(bgLum, textLum);
        System.out.printf("region [%d,%d %dx%d] bgLum(modal)=%.3f avg=%.3f %s -> contrast=%.2f:1 %s%n",
                x0, y0, w, h, bgLum, sum / n, textDesc, ratio, verdict(ratio));
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

    /** Relative luminance of an opaque sRGB colour (WCAG 2.x). */
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

    private static double contrastRatio(double a, double b) {
        double lighter = Math.max(a, b);
        double darker = Math.min(a, b);
        return (lighter + 0.05) / (darker + 0.05);
    }
}
