package util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Direction {
  HORIZONTAL(0, -1, 0, 1), VERTICAL(-1, 0, 1, 0), LEFTRIGHT(-1, 1, 1, -1), RIGHTLEFT(-1, -1, 1, 1);
  private final int x1;
  private final int y1;
  private final int x2;
  private final int y2;

  public static Direction of(double angle) {
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
