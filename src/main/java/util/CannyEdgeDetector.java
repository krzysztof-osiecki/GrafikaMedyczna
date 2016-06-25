package util;

import data.ThresholdType;
import javaslang.Tuple;
import javaslang.Tuple2;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

public class CannyEdgeDetector {
  public static BufferedImage performCannyDetection(BufferedImage read, Double sigma, Integer low, Integer high, int[][] magnitude,
			Direction[][] direction) {
    Mat mat = OpenCvUtil.bufferedImageToMat(read);
    Mat grayMat = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
    Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);
    Imgproc.GaussianBlur(grayMat, grayMat, new Size(), sigma);
    Mat dx = new Mat();
    Mat dy = new Mat();
    Imgproc.Sobel(grayMat, dx, -1, 1, 0, 3, 1, 0);
    Imgproc.Sobel(grayMat, dy, -1, 0, 1, 3, 1, 0);
    int[][] xAsIntArray = ValueConverter.getIntArray(dx);
    int[][] yAsIntArray = ValueConverter.getIntArray(dy);
    calulateMagnitudeAndDirection(grayMat, xAsIntArray, yAsIntArray, magnitude, direction);
    Mat result = new Mat(grayMat.rows(), grayMat.cols(), CvType.CV_8UC1);
    result.put(0, 0, ValueConverter.toByteArray(ValueConverter.make1D(magnitude, grayMat.rows(), grayMat.cols())));
    result = OpenCvUtil.threshold(result, -1, 255, ThresholdType.OTSU_2D, ThresholdType.THRESH_TOZERO);
    int[][] mag = ValueConverter.getIntArray(result);
    suppressNonMaxPixels(mag, direction);
    int[] ints =
        performHysteresis(ValueConverter.make1D(mag, grayMat.rows(), grayMat.cols()), low, high, grayMat.height(),
            grayMat.width());
    result.put(0, 0, ValueConverter.toByteArray(ints));
    return OpenCvUtil.byteMat2BufferedImage(result);
  }

  private static int[] performHysteresis(int[] mag, int low, int high, int height, int width) {
    int[] data = new int[height * width];
    int offset = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (data[offset] == 0 && mag[offset] >= high) {
          follow(data, mag, x, y, offset, low, width, height);
        }
        offset++;
      }
    }
    return data;
  }

  private static void follow(int[] data, int[] mag, int x1, int y1, int i1, int threshold, int width, int height) {
    int x0 = x1 == 0 ? x1 : x1 - 1;
    int x2 = x1 == width - 1 ? x1 : x1 + 1;
    int y0 = y1 == 0 ? y1 : y1 - 1;
    int y2 = y1 == height - 1 ? y1 : y1 + 1;
    data[i1] = mag[i1];
    for (int x = x0; x <= x2; x++) {
      for (int y = y0; y <= y2; y++) {
        int i2 = x + y * width;
        if ((y != y1 || x != x1)
            && data[i2] == 0
            && mag[i2] >= threshold) {
          follow(data, mag, x, y, i2, threshold, width, height);
          return;
        }
      }
    }
  }

  private static void suppressNonMaxPixels(int[][] mag, Direction[][] direction) {
    for (int x = 0; x < mag.length; x++) {
      for (int y = 0; y < mag[x].length; y++) {
        if (mag[x][y] > 0) {
          Tuple2<Integer, Integer> firstNeibourgh = Tuple.of(
              x + direction[x][y].getX1() < 0 || x + direction[x][y].getX1() >= mag.length ? x :
                  x + direction[x][y].getX1(),
              y + direction[x][y].getY1() < 0 || y + direction[x][y].getY1() >= mag[x].length ? y :
                  y + direction[x][y].getY1()
          );
          Tuple2<Integer, Integer> secondNeibourgh = Tuple.of(
              x + direction[x][y].getX2() < 0 || x + direction[x][y].getX2() >= mag.length ? x :
                  x + direction[x][y].getX2(),
              y + direction[x][y].getY2() < 0 || y + direction[x][y].getY2() >= mag[x].length ? y :
                  y + direction[x][y].getY2()
          );
          if (mag[x][y] < mag[firstNeibourgh._1][firstNeibourgh._2]
              || mag[x][y] < mag[secondNeibourgh._1][secondNeibourgh._2]) {
            mag[x][y] = 0;
          }
        }
      }
    }
  }

  private static void calulateMagnitudeAndDirection(Mat grayMat, int[][] xAsIntArray, int[][] yAsIntArray,
                                                    int[][] magnitude,
                                                    Direction[][] direction) {
    for (int x = 0; x < grayMat.rows(); x++) {
      for (int y = 0; y < grayMat.cols(); y++) {
        int hypot = (int) Math.hypot(xAsIntArray[x][y], yAsIntArray[x][y]);
        double v = Math.toDegrees(Math.atan2(yAsIntArray[x][y], xAsIntArray[x][y]));
        magnitude[x][y] = hypot;
        direction[x][y] = Direction.of(v);
      }
    }
  }

}