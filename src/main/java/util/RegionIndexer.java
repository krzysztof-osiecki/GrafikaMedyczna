package util;


import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class RegionIndexer {

  private static Random random = new Random();

  public static BufferedImage indexRegions(Mat imread) {
    int width = imread.rows();
    int height = imread.cols();
    int highestRegion = 1;
    byte[] data = new byte[width * height];
    imread.get(0, 0, data);
    int[] ints = new int[data.length];
    for (int i = 0; i < data.length; i++) {
      ints[i] = ((int) data[i]) == 0 ? 1 : 0;
    }
    int[][] data2s = ValueConverter.make2D(ints, width, height);
    for (int y = 0; y < width; y++) {
      for (int x = 0; x < height; x++) {
        if (data2s[y][x] != 0) {
          int top = Integer.MAX_VALUE;
          int left = Integer.MAX_VALUE;
          if (x - 1 >= 0) {
            left = data2s[y][x - 1];
          }
          if (y - 1 >= 0) {
            top = data2s[y - 1][x];
          }
          if ((top == 0 && left == 0) || (top == 0 && left == Integer.MAX_VALUE) ||
              (top == Integer.MAX_VALUE && left == 0) || (top == Integer.MAX_VALUE && left == Integer.MAX_VALUE)) {
            //sasiedzi nie pokolorowani albo nie istniejacy
            data2s[y][x] = highestRegion;
            highestRegion++;
          } else {
            if (left < top) {
              if (left != 0) {
                data2s[y][x] = left;
                replace(data2s, top, left);
                //zamien wszystkie topy na lefty
              } else {
                data2s[y][x] = top;
                //laczymy sie tylko jednym pikselem na gorze
              }
            } else if (left > top) {
              if (top != 0) {
                data2s[y][x] = top;
                replace(data2s, left, top);
              } else {
                data2s[y][x] = left;
                //zamien wszystkie lefty na topy
              }
            } else {
              //left == top
              data2s[y][x] = left;
            }
          }
        }
      }
    }
    java.util.List<Color> colors = new ArrayList<>();
    for (int i = 0; i < highestRegion; i++) {
      colors.add(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
    }
    Mat mat = new Mat(imread.rows(), imread.cols(), CvType.CV_8SC3);
    byte[] bytes = new byte[imread.rows() * imread.cols() * 3];
    int index = 0;
    for (int[] data2 : data2s) {
      for (int aData2 : data2) {
        if (aData2 != 0) {
          Color color = colors.get(aData2);
          bytes[index] = (byte) color.getRed();
          bytes[index + 1] = (byte) color.getGreen();
          bytes[index + 2] = (byte) color.getBlue();
        } else {
          bytes[index] = (byte) 255;
          bytes[index + 1] = (byte) 255;
          bytes[index + 2] = (byte) 255;
        }
        index += 3;
      }
    }
    mat.put(0, 0, bytes);
    return OpenCvUtil.byteMat2RgbBufferedImage(mat);
  }

  private static void replace(int[][] array, int from, int to) {
    for (int i = 0; i < array.length; i++) {
      for (int j = 0; j < array[i].length; j++) {
        array[i][j] = array[i][j] == from ? to : array[i][j];
      }
    }
  }

}
