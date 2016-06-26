package util;

public class Direction {
	private final int x1;
	private final int y1;
	private final int x2;
	private final int y2;
	private double degree;

	public double getDegree() {
		return degree;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}

	public int getX2() {
		return x2;
	}

	public int getY2() {
		return y2;
	}

	Direction(int x1, int y1, int x2, int y2, double degree) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.degree = degree;
	}

	public static Direction of(double angle) {
		if (angle < 22.5 || angle >= 157.5) {
			return new Direction(0, -1, 0, 1, angle);
		}
		if (angle >= 22.5 && angle < 67.5) {
			return new Direction(-1, 1, 1, -1, angle);
		}
		if (angle >= 67.5 && angle < 112.5) {
			return new Direction(-1, 0, 1, 0, angle);
		}
		return new Direction(-1, -1, 1, 1, angle);
	}
}
