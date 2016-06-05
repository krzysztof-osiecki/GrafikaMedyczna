package util;

import org.opencv.core.*;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.opencv.imgproc.Imgproc.THRESH_BINARY_INV;

public class OpenCvUtil {

	private static Random random = new Random();

	private static List<Color> colors = Arrays.asList(Color.green, Color.blue, Color.black, Color.red, Color.cyan, Color.yellow, Color.magenta);

	public static BufferedImage shortMat2BufferedImage(Mat m) {
		int bufferSize = m.channels() * m.cols() * m.rows();
		short[] b = new short[bufferSize];
		m.get(0, 0, b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), BufferedImage.TYPE_USHORT_GRAY);
		final short[] targetPixels = ((DataBufferUShort) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public static BufferedImage intMat2BufferedImage(Mat m) {
		int bufferSize = m.channels() * m.cols() * m.rows();
		int[] b = new int[bufferSize];
		m.get(0, 0, b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), BufferedImage.TYPE_USHORT_GRAY);
		final int[] targetPixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public static BufferedImage ioubleMat2BufferedImage(Mat m) {
		int bufferSize = m.channels() * m.cols() * m.rows();
		double[] b = new double[bufferSize];
		m.get(0, 0, b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), BufferedImage.TYPE_USHORT_GRAY);
		final double[] targetPixels = ((DataBufferDouble) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

	public static BufferedImage floatMat2BufferedImage(Mat m) {
		int bufferSize = m.channels() * m.cols() * m.rows();
		float[] b = new float[bufferSize];
		m.get(0, 0, b);
		BufferedImage image = new BufferedImage(m.cols(), m.rows(), BufferedImage.TYPE_4BYTE_ABGR);
		final float[] targetPixels = ((DataBufferFloat) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return image;
	}

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

	public static Mat calculateThreshold(Mat imread, int levels, int max) {
		double initialTreshold = max;
		byte[] v = new byte[imread.cols() * imread.rows() * 3];
		Mat mat = new Mat(imread.rows(), imread.cols(), CvType.CV_8UC3, new Scalar(0));
		for (int i = 0; i < levels; i++) {
			Mat clone = imread.clone();
			Imgproc.threshold(clone, clone, initialTreshold, max, THRESH_BINARY_INV);
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
			initialTreshold -= max / (double) levels;
		}
		mat.put(0, 0, v);
		return mat;
	}

	private static boolean allZeros(double[] array) {
		for (double val : array) {
			if (val != 0) {
				return false;
			}
		}
		return true;
	}

	public static BufferedImage incContrastBySubstaction(BufferedImage image1, BufferedImage image2) {
		BufferedImage colorImage1 = new BufferedImage(image1.getWidth(), image1.getHeight(), BufferedImage.TYPE_INT_RGB);
		colorImage1.getGraphics().drawImage(image1, 0, 0, null);
		BufferedImage colorImage2 = new BufferedImage(image2.getWidth(), image2.getHeight(), BufferedImage.TYPE_INT_RGB);
		colorImage2.getGraphics().drawImage(image2, 0, 0, null);
		BufferedImage substract = ImageHelper.substract(colorImage2, colorImage1, 5);
		return ImageHelper.substract(substract, colorImage1, 1);
	}

	public static Mat bufferedImageToMat(BufferedImage read) {
		byte[] pixels = ((DataBufferByte) read.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(read.getHeight(), read.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, pixels);
		return mat;
	}


	public static Mat bufferedImageToMatInt(BufferedImage read) {
		int[] pixels = ((DataBufferInt) read.getRaster().getDataBuffer()).getData();
		byte[] perChannels = new byte[pixels.length * 3];
		int i = 0;
		for (int pixel : pixels) {
			int jred = ImageHelper.jred(pixel);
			int jgreen = ImageHelper.jgreen(pixel);
			int jblue = ImageHelper.jblue(pixel);
			perChannels[i] = (byte) jred;
			perChannels[i + 1] = (byte) jgreen;
			perChannels[i + 2] = (byte) jblue;
			i += 3;
		}
		Mat mat = new Mat(read.getHeight(), read.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, perChannels);
		return mat;
	}

	public static BufferedImage performHoughLinesDetection(File file, double hysteresisTresholdLow, double hysteresisTresholdHigh, double maskSize) {
		Mat imread = Highgui.imread(file.getAbsolutePath());
		Mat tresh = imread.clone();
		double[] doubles = imread.get(0, 0);
		tresh.put(0, 0, doubles);
		Mat lines = new Mat();
		Imgproc.Canny(tresh, tresh, hysteresisTresholdLow, hysteresisTresholdHigh);
		Imgproc.HoughLinesP(tresh, lines, maskSize, Math.PI / 180.0, 80, 30, 10);
		Imgproc.cvtColor(tresh, tresh, Imgproc.COLOR_GRAY2RGB);
		for (int x = 0; x < lines.cols(); x++) {
			double[] vec = lines.get(0, x);
			double x1 = vec[0],
					y1 = vec[1],
					x2 = vec[2],
					y2 = vec[3];
			org.opencv.core.Point start = new org.opencv.core.Point(x1, y1);
			org.opencv.core.Point end = new org.opencv.core.Point(x2, y2);
			Core.line(imread, start, end, new Scalar(0, 255, 0), 1);
		}
		return OpenCvUtil.byteMat2RgbBufferedImage(imread);
	}


	private static int[][] normalMake2D(int[] array, int width, int height) {
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

	private static int[][] make2D(int[] array, int width, int height) {
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

	private static int[] make1D(int[][] array, int width, int height) {
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

	private static byte[][] make2D(byte[] array, int width, int height) {
		byte[][] ints = new byte[width][height];
		int i = 0;
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				ints[x][y] = array[i];
				i++;
			}
		}
		return ints;
	}

	private static byte[] make1D(byte[][] array, int width, int height) {
		byte[] ints = new byte[width * height];
		int i = 0;
		for (int y = 0; y < height; ++y) {
			for (int x = 0; x < width; ++x) {
				ints[i] = array[x][y];
				i++;
			}
		}
		return ints;
	}

	public static int[] doHilditchsThinning(int[] data, int width, int height) {
		int[][] binaryImage = make2D(data, width, height);
		int[] copy = new int[width * height];
		System.arraycopy(data, 0, copy, 0, width * height);
		int[][] bu = make2D(copy, width, height);
		int a, b;
		boolean hasChange;
		do {
			hasChange = false;
			for (int y = 1; y + 1 < binaryImage.length; y++) {
				for (int x = 1; x + 1 < binaryImage[y].length; x++) {
					a = getA(binaryImage, y, x);
					b = getB(binaryImage, y, x);
					if ((binaryImage[y][x] == 1) && ((2 <= b) && (b <= 6)) && (a == 1) &&
							((binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y][x - 1] == 0) || (getA(binaryImage, y - 1, x) != 1)) &&
							((binaryImage[y - 1][x] * binaryImage[y][x + 1] * binaryImage[y + 1][x] == 0) || (getA(binaryImage, y, x + 1) != 1))) {
						bu[y][x] = 0;
						hasChange = true;
					}
				}
			}
			int[] ints = make1D(bu, width, height);
			binaryImage = make2D(ints, width, height);
		} while (hasChange);

		return make1D(binaryImage, width, height);
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
		if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y][x + 1] == 0 && binaryImage[y + 1][x + 1] == 1) {
			count++;
		}
		//p5 p6
		if (y + 1 < binaryImage.length && x + 1 < binaryImage[y].length && binaryImage[y + 1][x + 1] == 0 && binaryImage[y + 1][x] == 1) {
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
		return binaryImage[y - 1][x] + binaryImage[y - 1][x + 1] + binaryImage[y][x + 1] + binaryImage[y + 1][x + 1] + binaryImage[y + 1][x] +
				binaryImage[y + 1][x - 1] + binaryImage[y][x - 1] + binaryImage[y - 1][x - 1];
	}

	public static BufferedImage indexRegions(Mat imread) {
		int width = imread.cols();
		int height = imread.rows();
		int highestRegion = 1;
		byte[] data = new byte[width * height];
		imread.get(0, 0, data);
		int[] ints = new int[data.length];
		for (int i = 0; i < data.length; i++) {
			ints[i] = ((int) data[i]) == 0 ? 1 : 0;
		}
		int[][] data2s = normalMake2D(ints, width, height);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				if (data2s[y][x] != 0) {
					int top = -1;
					int left = -1;
					if (x - 1 >= 0) {
						left = data2s[y][x - 1];
					}
					if (y - 1 >= 0) {
						top = data2s[y - 1][x];
					}
					data2s[y][x] = Math.max(left, top);
					if (data2s[y][x] == 0 || data2s[y][x] == -1) {
						data2s[y][x] = highestRegion;
					}
					if (data2s[y][x] == highestRegion) {
						highestRegion++;
					}
				}
			}
		}

		return null;
	}
}
