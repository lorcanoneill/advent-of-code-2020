package com.lorcan.advent.utils;

import java.util.Objects;

public class Point {
  private final int x;
  private final int y;

  public Point(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  @Override
  public String toString() {
    return String.format("%d, %d", x, y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Point)) {
      return false;
    }

    Point that = (Point) obj;
    return Objects.equals(this.getX(), that.getX())
        && Objects.equals(this.getY(), that.getY());
  }
}
