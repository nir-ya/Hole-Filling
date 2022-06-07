import java.awt.*;
import java.awt.image.BufferedImage;


public class ImageUtils {


    private static void convertImageToGrayscale(BufferedImage image) {
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                Color rgb = new Color(image.getRGB(x, y));
                int newColor = HoleFilling.rgbToGray(rgb);
                image.setRGB(x, y, new Color(newColor, newColor, newColor).getRGB());
            }
        }
    }


    private static void carveHole(BufferedImage image, BufferedImage mask) {
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int newColor = HoleFilling.rgbToGray(new Color(mask.getRGB(x, y)));

                if (newColor < HoleFilling.MASK_HOLE_THRESHOLD) {
                    image.setRGB(x, y, new Color(newColor, newColor, newColor).getRGB());
                }
            }
        }
    }
}
