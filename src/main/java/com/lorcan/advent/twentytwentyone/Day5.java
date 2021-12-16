package com.lorcan.advent.twentytwentyone;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day5 {

  private static final String INPUT = "twentytwentyone/day-5-input.txt";
  public static void main(String[] args) {
    List<Line> lines = LogAndLoad.readFile(INPUT)
        .stream()
        .map(Line::fromString)
        .collect(Collectors.toUnmodifiableList());

    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;

    for (Line line : lines) {
      minX = Math.min(minX, Math.min(line.x1, line.x2));
      maxX = Math.max(maxX, Math.max(line.x1, line.x2));

      minY = Math.min(minY, Math.min(line.y1, line.y2));
      maxY = Math.max(maxY, Math.max(line.y1, line.y2));
    }

    LogAndLoad.log("min X: %d, min Y: %d, max X: %d, max Y: %d", minX, minY, maxX, maxY);

    process(lines, minX, maxX, minY, maxY, Line::isHorizontalOrVertical);
    process(lines, minX, maxX, minY, maxY, Line::isDiagonalHorizontalOrVertical);
  }

  private static void process(List<Line> lines,
                              int minX,
                              int maxX,
                              int minY,
                              int maxY,
                              Function<Line, Boolean> filter) {
    int pointsWithLinesOverlapping = 0;
    for (int x = minX; x <= maxX; x++) {
      for (int y = minY; y <= maxY; y++) {
        final int finalX = x;
        final int finalY = y;

        List<Line> linesThatThePointIsOn = lines.stream()
            .filter(filter::apply)
            .filter(line -> line.isPointOnLine(finalX, finalY))
            .collect(Collectors.toUnmodifiableList());

        if (linesThatThePointIsOn.size() >= 2) {
          pointsWithLinesOverlapping++;
        }
      }
    }

    LogAndLoad.log("Found %d points with lines overlapping", pointsWithLinesOverlapping);
  }

  static class Line {
    private final int x1;
    private final int y1;
    private final int x2;
    private final int y2;
    private final double slope;

    public static Line fromString(String str) {
      String[] arr = str.split(" -> ");
      return new Line(
          Integer.parseInt(arr[0].split(",")[0]),
          Integer.parseInt(arr[0].split(",")[1]),
          Integer.parseInt(arr[1].split(",")[0]),
          Integer.parseInt(arr[1].split(",")[1])
          );
    }

    public Line(int x1, int y1, int x2, int y2) {
      this.x1 = x1;
      this.y1 = y1;
      this.x2 = x2;
      this.y2 = y2;
      this.slope = getSlope(x1, y1, x2, y2);
    }

    public boolean isPointOnLine(int x, int y) {
      if (x1 == x && y1 == y) {
        return true;
      }

      if (x2 == x && y2 == y) {
        return true;
      }


      Line lineWithPointOne = new Line(x, y, x1, y1);
      Line lineWithPointTwo = new Line(x, y, x2, y2);

      if (lineWithPointOne.slope != lineWithPointTwo.slope) {
        return false;
      }

      if ((lineWithPointOne.isHorizontal() && !lineWithPointTwo.isHorizontal()) ||
          (!lineWithPointOne.isHorizontal() && lineWithPointTwo.isHorizontal())) {
        return false;
      }

      return ((x1 <= x && x <= x2)
          || (x1 >= x && x >= x2))
          && (
            (y1 <= y && y <= y2) ||
            (y1 >= y && y >= y2)
          );
    }

    public boolean isHorizontalOrVertical() {
      return isHorizontal() || isVertical();
    }

    public boolean isHorizontal() {
      return x1 == x2;
    }

    public boolean isVertical() {
      return y1 == y2;
    }

    public boolean isDiagonalHorizontalOrVertical() {
      return isHorizontalOrVertical() || slope == 1 || slope == -1;
    }

    @Override
    public String toString() {
      return String.format("%d, %d -> %d, %d", x1, y1, x2, y2);
    }
  }

  private static double getSlope(int x1, int y1, int x2, int y2) {
    if (y1 == y2) {
      return 0;
    }

    if (x1 == x2) {
      return 0;
    }

    return (double) (x1 - x2) / (double) (y1 - y2);
  }
}
