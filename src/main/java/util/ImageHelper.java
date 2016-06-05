package util;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class ImageHelper {
	public static int jrgb(int r, int g, int b) {
		return (r << 16) + (g << 8) + b;
	}

	public static int jred(int rgb) {
		return ((rgb >> 16) & 0xff);
	}

	public static int jgreen(int rgb) {
		return ((rgb >> 8) & 0xff);
	}

	public static int jblue(int rgb) {
		return (rgb & 0xff);
	}

	public static int[] getBitMap(BufferedImage src) {
		DataBufferInt sbuff = (DataBufferInt) src.getRaster().getDataBuffer();
		return sbuff.getData();
	}

	public static BufferedImage substract(BufferedImage src, BufferedImage dst, int scale) {
		int[] sp = getBitMap(src);
		int[] dp = getBitMap(dst);
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_RGB);
		int[] resultBitmap = getBitMap(result);
		int i = 0;
		for (int y = 0; y < src.getHeight(); ++y) {
			for (int x = 0; x < src.getWidth(); ++x) {
				resultBitmap[i] = substract(dp[i], sp[i], scale);
				i++;
			}
		}
		return result;
	}

	private static int substract(int a, int b, int scale) {
		int jblue = clump(jblue(a) - jblue(b), scale);// > 0 ? jblue(a) - jblue(b) : 0;//jblue(b) - jblue(a);
		int jgreen = clump(jgreen(a) - jgreen(b), scale);//jgreen(a) - jgreen(b) > 0 ? jgreen(a) - jgreen(b) : 0;//jgreen(b) - jgreen(a);
		int jred = clump(jred(a) - jred(b), scale);//jred(a) - jred(b) > 0 ? jred(a) - jred(b) : 0;//jred(b) - jred(a);
		return jrgb(jred, jgreen, jblue);
	}

	public static BufferedImage scale(BufferedImage image) {
		int[] sp = getBitMap(image);
		int i = 0;
		int maxBlue = 0, maxRed = 0, maxGreen = 0;
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				int jrgb = sp[i];
				maxBlue = jblue(jrgb) > maxBlue ? jblue(jrgb) : maxBlue;
				maxGreen = jgreen(jrgb) > maxGreen ? jgreen(jrgb) : maxGreen;
				maxRed = jred(jrgb) > maxRed ? jred(jrgb) : maxRed;
				i++;
			}
		}
		i = 0;
		for (int y = 0; y < image.getHeight(); ++y) {
			for (int x = 0; x < image.getWidth(); ++x) {
				int jrgb = sp[i];
				sp[i] = jrgb(
						255 - integerize(255 * (jred(jrgb) / (double) maxRed)),
						255 - integerize(255 * (jgreen(jrgb) / (double) maxGreen)),
						255 - integerize(255 * (jblue(jrgb) / (double) maxBlue))
				);
				i++;
			}
		}
		return image;
	}

	private static int clump(int val, int factor) {
		val *= factor;
		if (val < 0) {
			return 0;
		}
		if (val > 255) {
			return 255;
		}
		return val;
	}

	public static int integerize(double v) {
		Double floor = Math.floor(v);
		return floor.intValue();
	}

	public static void rewrite(BufferedImage read, int[] ints) {
		int[] bitMap = getBitMap(read);
		System.arraycopy(ints, 0, bitMap, 0, ints.length);
	}
}