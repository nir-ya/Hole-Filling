
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class HoleFilling {


    public static final int MAX_RGB = 255;
    public static final double MASK_HOLE_THRESHOLD = MAX_RGB * 0.5;


    static int rgbToGray(Color rgb) {
        return (int) (rgb.getRed() * 0.299 + rgb.getGreen() * 0.587 + rgb.getBlue() * 0.114);
    }


    /***
     * takes the original image and the mask, and produces a float 2D array that represents
     * the grayscale image after carving the hole according to the mask,
     * @param image the original image
     * @param mask defines the hole
     * @return a float 2D array (matrix) representing the grayscale image after carving the hole.
     */
    private static float[][] mergeToGrayscaleMatrix(BufferedImage image, BufferedImage mask) {
        float[][] matrix = new float[image.getWidth()][image.getHeight()];

        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int maskGrayValue = rgbToGray(new Color(mask.getRGB(x, y)));
                int imageGrayValue = rgbToGray(new Color(image.getRGB(x, y)));

                if (maskGrayValue < MASK_HOLE_THRESHOLD) {
                    matrix[x][y] = HoleFiller.HOLE_VALUE;
                }
                else {
                    matrix[x][y] = imageGrayValue / (float) MAX_RGB;
                }
            }
        }
        return matrix;
    }

    private static BufferedImage imageFromMatrix(BufferedImage image, float[][] matrix) {
        for (int x = 0; x < image.getWidth(); ++x) {
            for (int y = 0; y < image.getHeight(); ++y) {
                int newColor = (int) (matrix[x][y] * MAX_RGB);
                image.setRGB(x, y, new Color(newColor, newColor, newColor).getRGB());
            }
        }
        return image;
    }


    private static int z;
    private static float epsilon;

    public static float defaultWeight(Pixel u, Pixel v) {
        float dx = u.getX() - v.getX();
        float dy = u.getY() - v.getY();
        float distance = (float) Math.sqrt(dx*dx + dy*dy);
        return 1 / (float) (Math.pow(distance, z) + epsilon);
    }


    public static void main(String[] args) {

        try {
            BufferedImage image = ImageIO.read(new File(args[0]));
            BufferedImage mask = ImageIO.read(new File(args[1]));
            z = Integer.parseInt(args[2]);
            epsilon = Float.parseFloat(args[3]);
            HoleFiller.PixelConnectivity connectivity = HoleFiller.PixelConnectivity.valueOf(args[4]);

            float[][] matrix = mergeToGrayscaleMatrix(image, mask);

            HoleFiller filler = new HoleFiller(HoleFilling::defaultWeight, matrix, connectivity);
            filler.execute();
//            filler.approximateSolution(55);

            ImageIO.write(imageFromMatrix(image, matrix), "png", new File("result.png"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
