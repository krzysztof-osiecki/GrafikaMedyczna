package util;


public class HilditchsThinner {

  public static int[] performHilditchsThinning(int[] data, int width, int height) {
    int[][] binaryImage = make2DwithThresh(data, width, height);
    int[] copy = new int[width * height];
    System.arraycopy(data, 0, copy, 0, width * height);
    int[][] bu = make2DwithThresh(copy, width, height);
    int a, b;
    boolean hasChange;
    do {
      hasChange = false;
      for (int y = 1; y + 1 < binaryImage.length; y++) {
        for (int x = 1; x + 1 < binaryImage[y].length; x++) {
          a = getA(binaryImage, y, x);
          b = getB(binaryImage, y, x);
          if ((binaryImage[y][x] == 1) && ((2 <= b) && (b <= 6)) && (a == 1) &&
              ((binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y][x - 1] == 0) ||
                  (getA(binaryImage, y - 1, x) != 1)) &&
              ((binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y + 1][x] == 0) ||
                  (getA(binaryImage, y, x + 1) != 1))) {
            bu[y][x] = 0;
            hasChange = true;
          }
        }
      }
      int[] ints = make1DWithThresh(bu, width, height);
      binaryImage = make2DwithThresh(ints, width, height);
    } while (hasChange);

    return make1DWithThresh(binaryImage, width, height);
  }

  private static int[][] make2DwithThresh(int[] array, int width, int height) {
    int[][] ints = new int[width][height];
    int i = 0;
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        ints[x][y] = array[i] > 16777215 / 2 ? 0 : 1;
        i++;
      }
    }
    return ints;
  }

  private static int[] make1DWithThresh(int[][] array, int width, int height) {
    int[] ints = new int[width * height];
    int i = 0;
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        ints[i] = array[x][y] == 0 ? 16777215 : 0;
        i++;
      }
    }
    return ints;
  }

  private static int getA(int[][] binaryImage, int y, int x) {
    int count = 0;
    //p2 p3
    if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x] == 0 && binaryImage[y - 1][x + 1] == 1) {
      count++;
    }
    //p3 p4
    if (y - 1 >= 0 && x + 1 < binaryImage[y].length && binaryImage[y - 1][x + 1] == 0 && binaryImage[y][x + 1] == 1) {
      count++;
    }
    //p4 p5
    if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y][x + 1] == 0 &&
        binaryImage[y + 1][x + 1] == 1) {
      count++;
    }
    //p5 p6
    if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y + 1][x + 1] == 0 &&
        binaryImage[y + 1][x] == 1) {
      count++;
    }
    //p6 p7
    if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x] == 0 && binaryImage[y + 1][x - 1] == 1) {
      count++;
    }
    //p7 p8
    if (y + 1 < binaryImage.length && x - 1 >= 0 && binaryImage[y + 1][x - 1] == 0 && binaryImage[y][x - 1] == 1) {
      count++;
    }
    //p8 p9
    if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y][x - 1] == 0 && binaryImage[y - 1][x - 1] == 1) {
      count++;
    }
    //p9 p2
    if (y - 1 >= 0 && x - 1 >= 0 && binaryImage[y - 1][x - 1] == 0 && binaryImage[y - 1][x] == 1) {
      count++;
    }
    return count;
  }

  private static int getB(int[][] binaryImage, int y, int x) {
    return binaryImage[y - 1][x] + binaryImage[y - 1][x + 1] + binaryImage[y][x + 1] + binaryImage[y + 1][x + 1] +
        binaryImage[y + 1][x] + binaryImage[y + 1][x - 1] + binaryImage[y][x - 1] + binaryImage[y - 1][x - 1];
  }

}
