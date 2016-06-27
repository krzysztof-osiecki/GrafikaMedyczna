package util;

import data.ThresholdType;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Arrays;
import java.util.List;

public class OpenCvUtil {

  private static List<Color> colors =
      Arrays.asList(Color.green, Color.blue, Color.black, Color.red, Color.cyan, Color.yellow, Color.magenta);

  public static BufferedImage byteMat2BufferedImage(Mat m) {
    int bufferSize = m.channels() * m.cols() * m.rows();
    byte[] b = new byte[bufferSize];
    m.get(0, 0, b);
    BufferedImage image = new BufferedImage(m.cols(), m.rows(), BufferedImage.TYPE_BYTE_GRAY);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(b, 0, targetPixels, 0, b.length);
    return image;
  }

  public static BufferedImage byteMat2RgbBufferedImage(Mat m) {
    int bufferSize = m.channels() * m.cols() * m.rows();
    byte[] b = new byte[bufferSize];
    m.get(0, 0, b);
    BufferedImage image = new BufferedImage(m.cols(), m.rows(), BufferedImage.TYPE_3BYTE_BGR);
    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    System.arraycopy(b, 0, targetPixels, 0, b.length);
    return image;
  }

  public static Mat byteArrayToMat8U1(byte[] bytes) {
    Mat mat = new Mat(828, 512, CvType.CV_8UC1, new Scalar(0));
    int low, high;
    int x = 0;
    double values[] = new double[512 * 828 * 2];
    for (int i = 0; i < bytes.length; i += 2) {
      low = bytes[i] & 0xff;
      high = bytes[i + 1] & 0xff;
      double c = (double) ((high << 8) + (low & 0x00ff));
      double v = (c / (double) (1 << 16)) * 255;
      values[x] = v;
      x++;
    }
    mat.put(0, 0, values);
    return mat;
  }

  public static Mat threshold(Mat src, double thresh, double max, ThresholdType type, ThresholdType alternative) {
    byte[] dataArray = new byte[src.cols() * src.rows() * src.channels()];
    int[] resultArray = new int[src.cols() * src.rows() * src.channels()];
    src.get(0, 0, dataArray);
    int[] intData = ValueConverter.toIntArray(dataArray);
    if (type == ThresholdType.OTSU_2D) {
      thresh = calculate2dOtsu(intData, src.height(), src.width());
      type = alternative;
    } else if (type == ThresholdType.OTSU) {
      thresh = calculateOtsu(intData);
      type = alternative;
    }
    for (int i = 0; i < dataArray.length; i++) {
      switch (type) {
        case BINARY:
          resultArray[i] = (byte) (intData[i] > thresh ? max : 0);
          break;
        case INV_BINARY:
          resultArray[i] = (byte) (intData[i] > thresh ? 0 : max);
          break;
        case THRESH_TRUNC:
          resultArray[i] = intData[i] > thresh ? (byte) thresh : intData[i];
          break;
        case THRESH_TOZERO:
          resultArray[i] = intData[i] > thresh ? intData[i] : 0;
          break;
        case THRESH_TOZERO_INV:
          resultArray[i] = intData[i] > thresh ? 0 : intData[i];
          break;
      }
    }
    Mat resultMat = new Mat(src.rows(), src.cols(), src.type());
    resultMat.put(0, 0, ValueConverter.toByteArray(resultArray));
    return resultMat;
  }

  public static Mat calculateThreshold(Mat imread, int levels, int max, ThresholdType type, boolean color) {
    double initialTreshold = max;
    byte[] v = new byte[imread.cols() * imread.rows() * 3];
    Mat mat = new Mat(imread.rows(), imread.cols(), imread.type(), new Scalar(0));
    for (int i = 0; i < levels; i++) {
      Mat clone = imread.clone();
      clone = threshold(clone, initialTreshold, max - 1, type, ThresholdType.BINARY);
      if (color) {
        Imgproc.cvtColor(clone, clone, Imgproc.COLOR_GRAY2BGR);
        int k = 0;
        for (int y = 0; y < clone.rows(); y++) {
          for (int x = 0; x < clone.cols(); x++) {
            double[] doubles = clone.get(y, x);
            if (!allZeros(doubles)) {
              v[k] = (byte) colors.get(i).getBlue();
              v[k + 1] = (byte) colors.get(i).getGreen();
              v[k + 2] = (byte) colors.get(i).getRed();
            }
            k += 3;
          }
        }
      }
      initialTreshold -= max / (double) levels;
    }
    mat.put(0, 0, v);
    return mat;
  }

  public static BufferedImage incContrastBySubstaction(BufferedImage image1, BufferedImage image2) {
    BufferedImage colorImage1 = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
    colorImage1.getGraphics().drawImage(image1, 0, 0, null);
    BufferedImage colorImage2 = new BufferedImage(image2.getWidth(), image2.getHeight(), BufferedImage.TYPE_INT_RGB);
    colorImage2.getGraphics().drawImage(image2, 0, 0, null);
    BufferedImage substract = ImageHelper.substract(colorImage2, colorImage1, 5);
    return ImageHelper.substract(substract, colorImage1, 1);
  }

  public static Mat bufferedImageToMat(BufferedImage read, int type) {
    byte[] pixels = ((DataBufferByte) read.getRaster().getDataBuffer()).getData();
    Mat mat = new Mat(read.getHeight(), read.getWidth(), type);
    mat.put(0, 0, pixels);
    return mat;
  }

  private static int calculate2dOtsu(int[] intData, int width, int height) {
    int[][] histogram = new int[256][256];
    double[][] normalizedHistogram = new double[256][256];
    int[][] data = ValueConverter.make2D(intData, width, height);
    double maksWariancja = 0.0;
    int result = 0;
    int pixelCount = width * height;
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        histogram[data[x][y]][calculateNeighborsAverage(data, x, y, width, height)]++;
      }
    }
    for (int y = 0; y < 256; ++y) {
      for (int x = 0; x < 256; ++x) {
        normalizedHistogram[x][y] = histogram[x][y] / (double) pixelCount;
      }
    }
    double pTla = 0;
    double uTi = 0.0;
    double uTj = 0.0;
    double pObiektu = 0.0;
    double u0i = 0.0;
    double u0j = 0.0;
    double u1i;
    double u1j;
    for (int y = 0; y < 256; ++y) {
      for (int x = 0; x < 256; ++x) {
        pTla += normalizedHistogram[x][y];
        uTi += x * normalizedHistogram[x][y];
        uTj += y * normalizedHistogram[x][y];
      }
    }
    for (int t = 0; t < 256; t++) {
      for (int s = 0; s < 256; s++) {
        pObiektu += normalizedHistogram[t][s];
        pTla -= normalizedHistogram[t][s];
        u0i += (t * normalizedHistogram[t][s]);
        u1i = uTi - u0i;
        u0j += (s * normalizedHistogram[t][s]);
        u1j = uTj - u0j;

        double tr = pObiektu * (Math.pow((u0i / pObiektu - uTi), 2) + Math.pow((u0j / pObiektu - uTj), 2)) +
            pTla * (Math.pow((u1i / pTla - uTi), 2) + Math.pow((u1j / pTla - uTj), 2));
        if (tr > maksWariancja) {
          maksWariancja = tr;
          result = t;
        }
      }
    }
    return result;
  }

  private static int calculateNeighborsAverage(int[][] data, int x, int y, int width, int height) {
    int suma = 0;
    for (int k = -1; k <= 1; k++) {
      for (int l = -1; l <= 1; l++) {
        if (x + k > 0 && x + k < width) {
          if (y + l > 0 && y + l < height) {
            suma += data[x + k][y + l];
          }
        }
      }
    }
    return suma / 9;
  }

  private static double calculateOtsu(int[] intData) {
    int[] histogram = new int[256];
    double maksWariancja = 0.0;
    int result = 0;
    for (int value : intData) {
      histogram[value]++;
    }
    for (int t = 0; t < 256; t++) {
      int pObiektu = 0;
      double sredniaObiektu = 0.0;
      int pTla = 0;
      double sredniaTla = 0.0;
      for (int k = 0; k <= t; k++) {
        pObiektu += histogram[k];
      }
      for (int k = t + 1; k < 256; k++) {
        pTla += histogram[k];
      }
      for (int k = 0; k <= t; k++) {
        sredniaObiektu += (k * histogram[k]) / (double) pObiektu;
      }
      for (int k = t + 1; k < 256; k++) {
        sredniaTla += (k * histogram[k]) / (double) pTla;
      }
      double wewnatrzKlasowa = (pObiektu * pTla) * Math.pow(sredniaObiektu - sredniaTla, 2);
      if (wewnatrzKlasowa > maksWariancja) {
        maksWariancja = wewnatrzKlasowa;
        result = t;
      }
    }
    return result;
  }

  private static boolean allZeros(double[] array) {
    for (double val : array) {
      if (val != 0) {
        return false;
      }
    }
    return true;
  }
}
