package util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class LinesDetector {
	private static final int THETA_COUNT = 180;
	private static final double THRESHOLD = 750;
	private static double[] sinCache;
	private static double[] cosCache;

	static {
		sinCache = new double[THETA_COUNT];
		cosCache = sinCache.clone();
		for (int t = 0; t < THETA_COUNT; t++) {
			double realTheta = t * 180 / THETA_COUNT;
			sinCache[t] = Math.sin(realTheta);
			cosCache[t] = Math.cos(realTheta);
		}
	}

	public static BufferedImage performLineDetection(BufferedImage image, double sigma, int low, int high) {
		int[][] magnitude = new int[image.getHeight()][image.getWidth()];
		Direction[][] direction = new Direction[image.getHeight()][image.getWidth()];
		BufferedImage bufferedImage = CannyEdgeDetector.performCannyDetection(image, sigma, low, high, magnitude, direction);
		int houghHeight = (int) (Math.sqrt(2) * Math.max(image.getHeight(), image.getWidth())) / 2;
		int[][] houghArray = new int[THETA_COUNT][houghHeight];

		for (int x = 0; x < magnitude.length; x++) {
			for (int y = 0; y < magnitude[x].length; y++) {
				if ((magnitude[x][y] & 0x000000ff) != 0) {
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
		suppressNonMaxPixels(houghArray, 5);

		List<HoughLine> lines = new ArrayList<>();
		for (int t = 0; t < THETA_COUNT; t++) {
			for (int r = 0; r < houghHeight; r++) {
				if (houghArray[t][r] > THRESHOLD) {
					double theta = t * THETA_COUNT / 180;
					HoughLine e = new HoughLine(theta, r);
					lines.add(e);
					e.draw(bufferedImage, Color.RED.getRGB());
				}
			}
		}

		return bufferedImage;
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
