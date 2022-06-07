
import java.util.*;
import java.util.function.BiFunction;


public class HoleFiller {


    public static final int HOLE_VALUE = -1;

    public enum PixelConnectivity {FOUR, EIGHT}

    private final BiFunction<Pixel, Pixel, Float> weightFunction;
    private final float[][] image;
    private final PixelConnectivity connectivity;
    private final Collection<Pixel> holePixels;
    private Collection<Pixel> boundaryPixels;


    public HoleFiller(BiFunction<Pixel, Pixel, Float> weightFunc, float[][] image, PixelConnectivity conn) {
        this.weightFunction = weightFunc;
        this.image = image;
        this.connectivity = conn;
        holePixels = new ArrayList<>();
        boundaryPixels = new HashSet<>();
    }

    // exact solution
    public void execute() {
        findHoleAndBoundary();
        fillHole();
    }

    public void approximateSolution(int sampleSize) {
        findHoleAndBoundary();
        if (boundaryPixels.size() > sampleSize) {
            List<Pixel> boundaryList = new ArrayList<>(boundaryPixels);
            Collections.shuffle(boundaryList);
            boundaryPixels = boundaryList.subList(0, sampleSize);
        }
        fillHole();
    }

    private void fillHole() {
        for (Pixel pixel : holePixels) {
            float weightedColorSum = 0;
            float weightsSum = 0;

            for (Pixel boundaryPixel : boundaryPixels) {
                float weight = weightFunction.apply(pixel, boundaryPixel);
                weightedColorSum += weight * image[boundaryPixel.getX()][boundaryPixel.getY()];
                weightsSum += weight;
            }
            image[pixel.getX()][pixel.getY()] = weightedColorSum / weightsSum;
        }
    }

    private void findHoleAndBoundary() {
        for (int x = 0; x < image.length; ++x) {
            for (int y = 0; y < image[0].length; ++y) {
                if (image[x][y] == HOLE_VALUE) {
                    Pixel pixel = new Pixel(x, y);
                    holePixels.add(pixel);
                    addConnectedBoundaryPixels(x, y);
                }
            }
        }
    }

    private void addConnectedBoundaryPixels(int x, int y) {
        switch (connectivity) {
            case EIGHT:
                addDiagonalBoundaryPixels(x, y);
            case FOUR:
                addFourConnectedBoundaryPixels(x, y);
        }
    }

    private void addFourConnectedBoundaryPixels(int x, int y) {
        if (image[x - 1][y] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x-1, y));
        }
        if (image[x + 1][y] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x+1, y));
        }
        if (image[x][y + 1] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x, y+1));
        }
        if (image[x][y - 1] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x, y-1));
        }
    }

    private void addDiagonalBoundaryPixels(int x, int y) {
        if (image[x - 1][y - 1] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x-1, y-1));
        }
        if (image[x + 1][y - 1] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x+1, y-1));
        }
        if (image[x - 1][y + 1] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x-1, y+1));
        }
        if (image[x + 1][y - 1] != HOLE_VALUE) {
            boundaryPixels.add(new Pixel(x+1, y-1));
        }
    }
}
