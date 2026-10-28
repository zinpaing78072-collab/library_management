package com.example.tools.smoke;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Prints a coarse luminance map of an image, so the brightness variation
 * across the background photo can be judged region by region.
 *
 * Usage: LumaMap <image> [cols] [rows]
 */
public final class LumaMap {

    public static void main(String[] args) throws Exception {
        BufferedImage image = ImageIO.read(new File(args[0]));
        int cols = args.length > 1 ? Integer.parseInt(args[1]) : 12;
        int rows = args.length > 2 ? Integer.parseInt(args[2]) : 8;

        System.out.println(args[0] + "  " + image.getWidth() + "x" + image.getHeight());
        StringBuilder header = new StringBuilder("        ");
        for (int c = 0; c < cols; c++) {
            header.append(String.format("  c%-2d", c));
        }
        System.out.println(header);
        for (int r = 0; r < rows; r++) {
            StringBuilder line = new StringBuilder(String.format("  r%-2d  ", r));
            for (int c = 0; c < cols; c++) {
                int x0 = c * image.getWidth() / cols;
                int x1 = (c + 1) * image.getWidth() / cols;
                int y0 = r * image.getHeight() / rows;
                int y1 = (r + 1) * image.getHeight() / rows;
                double sum = 0;
                int n = 0;
                for (int y = y0; y < y1; y += 3) {
                    for (int x = x0; x < x1; x += 3) {
                        int argb = image.getRGB(x, y);
                        int rr = (argb >> 16) & 0xff;
                        int gg = (argb >> 8) & 0xff;
                        int bb = argb & 0xff;
                        sum += 0.2126 * rr + 0.7152 * gg + 0.0722 * bb;
                        n++;
                    }
                }
                int lum = (int) (sum / n);
                char marker = lum > 210 ? '#' : lum > 170 ? '+' : lum > 120 ? '-' : lum > 70 ? '.' : ' ';
                line.append(String.format(" %3d%c", lum, marker));
            }
            System.out.println(line);
        }
        System.out.println("  legend: ' ' <70 dark  '.' 70-120  '-' 120-170  '+' 170-210  '#' >210 bright");
    }
}
