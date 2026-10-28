package com.example.tools.smoke;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Reports luminance statistics for a region of an image, so the readability of
 * text over the background photo can be judged numerically.
 *
 * Usage: PixelProbe <image> <x> <y> <w> <h>
 */
public final class PixelProbe {

    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File(args[0]));
        int x0 = Integer.parseInt(args[1]);
        int y0 = Integer.parseInt(args[2]);
        int w = Integer.parseInt(args[3]);
        int h = Integer.parseInt(args[4]);

        double sum = 0;
        double min = 255;
        double max = 0;
        int n = 0;
        for (int y = y0; y < y0 + h && y < image.getHeight(); y++) {
            for (int x = x0; x < x0 + w && x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                int r = (argb >> 16) & 0xff;
                int g = (argb >> 8) & 0xff;
                int b = argb & 0xff;
                // Rec. 709 relative luminance
                double lum = 0.2126 * r + 0.7152 * g + 0.0722 * b;
                sum += lum;
                min = Math.min(min, lum);
                max = Math.max(max, lum);
                n++;
            }
        }
        double avg = sum / n;
        System.out.printf("region [%d,%d %dx%d] avgLum=%.1f min=%.1f max=%.1f%n", x0, y0, w, h, avg, min, max);
    }
}
