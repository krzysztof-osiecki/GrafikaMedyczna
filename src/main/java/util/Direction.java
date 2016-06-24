package util;

public enum Direction {
	HORIZONTAL(-1, 0, 1, 0), VERTICAL(0, -1, 0, 1), LEFTRIGHT(-1, -1, 1, 1), RIGHTLEFT(-1, 1, 1, -1);

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

	private final int x1;
	private final int y1;
	private final int x2;
	private final int y2;

	Direction(int x1, int y1, int x2, int y2) {

		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}

	public static Direction of(double angle) {
		if (angle > 180 || angle < 0) {
			throw new RuntimeException("jestes glupi");
		}
		if (angle < 22.5 || angle >= 157.5) {
			return HORIZONTAL;
		}
		if (angle >= 22.5 && angle < 67.5) {
			return RIGHTLEFT;
		}
		if (angle >= 67.5 && angle < 112.5) {
			return VERTICAL;
		}
		return LEFTRIGHT;
	}
}
