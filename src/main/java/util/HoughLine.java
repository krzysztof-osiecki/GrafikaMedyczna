package util;

import lombok.AllArgsConstructor;

import java.awt.*;
import java.awt.image.BufferedImage;

@AllArgsConstructor
public class HoughLine {

  protected double theta;
  protected double r;

  public void draw(BufferedImage image) {
    if (r < 20) return;
    Graphics2D g2d = image.createGraphics();
    g2d.setColor(Color.RED);
    Point pt1 = new Point();
    Point pt2 = new Point();
    double a = Math.cos(Math.toRadians(theta));
    double b = Math.sin(Math.toRadians(theta));
    double x0 = a * r, y0 = b * r;
    pt1.x = (int) (x0 + 1000 * (-b));
    pt1.y = (int) (y0 + 1000 * (a));
    pt2.x = (int) (x0 - 1000 * (-b));
    pt2.y = (int) (y0 - 1000 * (a));
    BasicStroke bs = new BasicStroke(2);
    g2d.setStroke(bs);
    g2d.drawLine(pt1.x, pt1.y, pt2.x, pt2.y);
  }
}