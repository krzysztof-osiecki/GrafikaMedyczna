package util;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LinesDetector {
  private static final int THETA_COUNT = 180;
  public static final int THETA_STEP = THETA_COUNT / 180;
  private static double[] sinCache;
  private static double[] cosCache;

  static {
    sinCache = new double[THETA_COUNT];
    cosCache = sinCache.clone();
    for (int t = 0; t < THETA_COUNT; t++) {
      double realTheta = t * THETA_STEP;
      sinCache[t] = Math.sin(Math.toRadians(realTheta));
      cosCache[t] = Math.cos(Math.toRadians(realTheta));
    }
  }

  public static BufferedImage performLineDetection(BufferedImage image, double sigma, int low, int high, int threshold,
                                                   int neighbourhood) {
    BufferedImage bufferedImage = CannyEdgeDetector.performCannyDetection(image, sigma, low, high);
    int houghHeight = 2 * (Math.max(image.getHeight(), image.getWidth()));
    int[][] houghArray = new int[THETA_COUNT][houghHeight];
    for (int x = 0; x < bufferedImage.getWidth(); x++) {
      for (int y = 0; y < bufferedImage.getHeight(); y++) {
        if ((bufferedImage.getRGB(x, y) & 0x000000ff) != 0) {
          for (int t = 0; t < THETA_COUNT; t++) {
            int r = (int) ((x * cosCache[t]) + (y * sinCache[t]));
            if (r < 0 || r >= houghHeight) {
              continue;
            }
            houghArray[t][r]++;
          }
        }
      }
    }
    suppressNonMaxPixels(houghArray, neighbourhood);

    List<HoughLine> lines = new ArrayList<>();
    for (int t = 0; t < THETA_COUNT; t++) {
      for (int r = 0; r < houghHeight; r++) {
        if (houghArray[t][r] > threshold) {
          double theta = t * THETA_STEP;
          HoughLine e = new HoughLine(theta, r);
          lines.add(e);
          e.draw(image);
        }
      }
    }

    return image;
  }

  private static void suppressNonMaxPixels(int[][] mag, int neighbour) {
    for (int x = 0; x < mag.length; x++) {
      for (int y = 0; y < mag[x].length; y++) {
        if (mag[x][y] > 0) {
          boolean proceed = true;
          for (int dx = -neighbour; dx < neighbour && proceed; dx++) {
            for (int dy = -neighbour; dy < neighbour && proceed; dy++) {
              if (x + dx > 0 && x + dx < mag.length && y + dy > 0 && y + dy < mag[x].length) {
                if (mag[x + dx][y + dy] > mag[x][y]) {
                  mag[x][y] = 0;
                  proceed = false;
                }
              }
            }
          }
        }
      }
    }
  }
}
