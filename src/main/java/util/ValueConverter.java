package util;

import org.opencv.core.Mat;

public class ValueConverter {
  public static int[][] getIntArray(Mat mat) {
    byte[] dyP = new byte[mat.rows() * mat.cols()];
    mat.get(0, 0, dyP);
    int i = 0;
    int[][] ints = new int[mat.rows()][mat.cols()];
    for (int x = 0; x < mat.rows(); x++) {
      for (int y = 0; y < mat.cols(); y++) {
        ints[x][y] = dyP[i] & 0xff;
        i++;
      }
    }
    return ints;
  }

  public static double[] make1D(double[][] array, int width, int height) {
    double[] ints = new double[width * height];
    int i = 0;
    for (double[] outerArray : array) {
      for (double value : outerArray) {
        ints[i] = value;
        i++;
      }
    }
    return ints;
  }

  public static int[] make1D(int[][] array, int width, int height) {
    int[] ints = new int[width * height];
    int i = 0;
    for (int[] outerArray : array) {
      for (int value : outerArray) {
        ints[i] = value;
        i++;
      }
    }
    return ints;
  }

  public static byte[] toByteArray(int[] array) {
    byte[] bytes = new byte[array.length];
    for (int i = 0; i < array.length; i++) {
      bytes[i] = (byte) array[i];
    }
    return bytes;
  }

  public static int[] toIntArray(byte[] array) {
    int[] ints = new int[array.length];
    for (int i = 0; i < array.length; i++) {
      ints[i] = array[i] & 0xff;
    }
    return ints;
  }


  public static int[][] make2D(int[] array, int width, int height) {
    int[][] ints = new int[width][height];
    int i = 0;
    for (int y = 0; y < width; ++y) {
      for (int x = 0; x < height; ++x) {
        ints[y][x] = array[i];
        i++;
      }
    }
    return ints;
  }
}
