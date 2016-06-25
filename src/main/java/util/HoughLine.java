package util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class HoughLine {

	protected double theta;
	protected double r;

	/**
	 * Initialises the hough line
	 */
	public HoughLine(double theta, double r) {
		this.theta = theta;
		this.r = r;
	}

	/**
	 * Draws the line on the image of your choice with the RGB colour of your choice.
	 */
	public void draw(BufferedImage image, int color) {
		Graphics2D g2d = image.createGraphics();
		g2d.setColor(Color.RED);
		double rho = this.r;
		double theta = this.theta;
		Point pt1 = new Point();
		Point pt2 = new Point();
		double a = Math.cos(theta);
		double b = Math.sin(theta);
		double x0 = a * rho, y0 = b * rho;
		pt1.x = (int) (x0 + 1000 * (-b));
		pt1.y = (int) (y0 + 1000 * (a));
		pt2.x = (int) (x0 - 1000 * (-b));
		pt2.y = (int) (y0 - 1000 * (a));
		BasicStroke bs = new BasicStroke(2);
		g2d.setStroke(bs);
		g2d.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
	}
}