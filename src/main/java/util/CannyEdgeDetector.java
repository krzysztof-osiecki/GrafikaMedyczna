package util;

import data.ThresholdType;
import javaslang.Tuple;
import javaslang.Tuple2;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;

public class CannyEdgeDetector {
  public static BufferedImage performCannyDetection(BufferedImage read, Double sigma, Integer low, Integer high) {
    Mat mat = OpenCvUtil.bufferedImageToMat(read, CvType.CV_8UC3);
    Mat grayMat = new Mat(mat.rows(), mat.cols(), CvType.CV_8UC1);
    Imgproc.GaussianBlur(mat, mat, new Size(), sigma);
    Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_BGR2GRAY);
    Mat dx = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type());
    Mat dy = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type());
    Imgproc.Sobel(grayMat, dx, CvType.CV_32F, 1, 0, 3, 1, 0, Imgproc.BORDER_DEFAULT);
    Imgproc.Sobel(grayMat, dy, CvType.CV_32F, 0, 1, 3, 1, 0, Imgproc.BORDER_DEFAULT);
    Direction[][] direction = new Direction[grayMat.rows()][grayMat.cols()];
    int[][] magnitude = calulateMagnitudeAndDirection(grayMat, dx, dy, direction);
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
    int i = 0;
    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        if (data[i] == 0 && mag[i] >= high) {
          follow(data, mag, x, y, i, low, width, height);
        }
        i++;
      }
    }
    return data;
  }

  private static void follow(int[] data, int[] mag, int x1, int y1, int i, int threshold, int width, int height) {
    data[i] = mag[i];
    for (int x = (x1 - 1 < 0 ? x1 : x1 - 1); x <= (x1 + 1 == width ? x1 : x1 + 1); x++) {
      for (int y = (y1 - 1 < 0 ? y1 : y1 - 1); y <= (y1 + 1 == height ? y1 : y1 + 1); y++) {
        int i2 = x + y * width;
        if ((y != y1 || x != x1) && data[i2] == 0 && mag[i2] >= threshold) {
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

  private static int[][] calulateMagnitudeAndDirection(Mat grayMat, Mat dx, Mat dy,
                                                       Direction[][] direction) {
    Mat mag = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type());
    Mat dxAbs = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type());
    Mat dyAbs = new Mat(grayMat.rows(), grayMat.cols(), grayMat.type());
    Core.magnitude(dx, dy, mag);
    Core.convertScaleAbs(dx, dxAbs);
    Core.convertScaleAbs(dy, dyAbs);
    Core.convertScaleAbs(mag, mag);

    int[][] xAsIntArray = ValueConverter.getIntArray(dxAbs);
    int[][] yAsIntArray = ValueConverter.getIntArray(dyAbs);
    int[][] magnitude = ValueConverter.getIntArray(mag);
    for (int x = 0; x < grayMat.rows(); x++) {
      for (int y = 0; y < grayMat.cols(); y++) {
        double v = Math.toDegrees(Math.atan2(yAsIntArray[x][y], xAsIntArray[x][y]));
        direction[x][y] = Direction.of(v);
      }
    }
    return magnitude;
  }

}
