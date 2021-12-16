package com.lorcan.advent.twentytwentyone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day9 {

  private static final String SAMPLE_INPUT = "twentytwentyone/day-9-sample-input.txt";
  private static final String INPUT = "twentytwentyone/day-9-input.txt";
  private static final String ANSI_RESET = "\u001B[0m";
  private static final String ANSI_RED = "\u001B[31m";
  private static final String ANSI_GREEN = "\u001B[32m";
  private static final String ANSI_BLUE = "\u001B[34m";

  public static void main(String[] args) {
    List<List<Integer>> sampleHeights = LogAndLoad.readFile(SAMPLE_INPUT)
        .stream()
        .map(str -> Arrays.stream(str.split("")).map(Integer::parseInt).collect(Collectors.toUnmodifiableList()))
        .collect(Collectors.toUnmodifiableList());

    List<List<Integer>> heights = LogAndLoad.readFile(INPUT)
        .stream()
        .map(str -> Arrays.stream(str.split("")).map(Integer::parseInt).collect(Collectors.toUnmodifiableList()))
        .collect(Collectors.toUnmodifiableList());

    List<LowPoint> sampleLowPoints = calculateLowPoints(sampleHeights);
    List<LowPoint> lowPoints = calculateLowPoints(heights);

//    partOne(sampleLowPoints);
//    partOne(lowPoints);

//    partTwo(sampleLowPoints, sampleHeights);
    partTwo(lowPoints, heights);
  }

  private static List<LowPoint> calculateLowPoints(List<List<Integer>> heights) {
    List<LowPoint> lowPoints = new ArrayList<>();
    for (int y = 0; y < heights.size(); y++) {
      List<Integer> theseHeights = heights.get(y);
      for (int x = 0; x < theseHeights.size(); x++) {
        // which points to check?
        // if x == 0 do not check before it
        // if x == theseHeights.size() - 1 do not check after it
        // if y == 0 do not check above it
        // if y == heights.size() - 1 do not check below it

        Set<Integer> adjacentHeights = new HashSet<>();
        if (x != 0) {
          adjacentHeights.add(theseHeights.get(x - 1));
        }

        if (x < theseHeights.size() - 1) {
          adjacentHeights.add(theseHeights.get(x + 1));
        }

        if (y != 0) {
          adjacentHeights.add(heights.get(y - 1).get(x));
        }

        if (y < heights.size() - 1) {
          adjacentHeights.add(heights.get(y + 1).get(x));
        }

        int thisHeight = theseHeights.get(x);
        if (adjacentHeights.stream().allMatch(height -> height > thisHeight)) {
          lowPoints.add(new LowPoint(x, y, thisHeight));
        }
      }
    }

    return lowPoints;
  }

  private static void partOne(List<LowPoint> lowPoints) {
    long sum = lowPoints.stream()
        .map(LowPoint::getValue)
        .reduce(0, (a, b) -> a + b + 1);
    LogAndLoad.log("Sum of risk level for %d low points: %d", lowPoints.size(), sum);
  }

  private static void partTwo(List<LowPoint> lowPoints, List<List<Integer>> heights) {
    List<Integer> basinSizes = new ArrayList<>();
    int maxX = heights.get(0).size();
    int maxY = heights.size();
    for (LowPoint lowPoint : lowPoints) {
      Set<Point> pointsInBasin = findPointsThatFlowDownToPoint(lowPoint.getPoint(), Optional.empty(), lowPoint.getValue(), maxX, maxY, heights);
      printMapOfPoints(lowPoint.getPoint(), pointsInBasin, heights);
      int sizeOfBasinFromLowPoint = pointsInBasin.size();
      LogAndLoad.log("Size of basin from low point %s: %d", lowPoint.getPoint(), sizeOfBasinFromLowPoint);
      basinSizes.add(sizeOfBasinFromLowPoint);
    }

    List<Integer> largestThreeBasinSizes = basinSizes.stream()
        .sorted(Comparator.reverseOrder())
        .limit(3)
        .collect(Collectors.toUnmodifiableList());

    long product = 1;
    for (int basinSize : largestThreeBasinSizes) {
      LogAndLoad.log("Basin size: %d", basinSize);
      product *= basinSize;
    }

    LogAndLoad.log("Product of three largest basin sizes: %d", product);
  }

  private static void printMapOfPoints(Point lowPoint, Set<Point> points, List<List<Integer>> heights) {
    int minX = points.stream()
        .map(Point::getX)
        .min(Comparator.naturalOrder()).get() - 1;
    int maxX = points.stream()
        .map(Point::getX)
        .max(Comparator.naturalOrder()).get() + 1;

    int minY = points.stream()
        .map(Point::getY)
        .min(Comparator.naturalOrder()).get() - 1;

    int maxY = points.stream()
        .map(Point::getY)
        .max(Comparator.naturalOrder()).get() + 1;

    int lengthOfLine = heights.get(0).size();

    LogAndLoad.log("Printing map for lowpoint %s with %d points", lowPoint, points.size());
    LogAndLoad.log("-".repeat(maxX - minX + 1));
    for (int y = minY; y <= maxY; y++) {
      if (y < 0 || y > heights.size() - 1) {
        continue;
      }

      for (int x = minX; x <= maxX; x++) {
        if (x < 0 || x > lengthOfLine - 1) {
          continue;
        }

        String value = String.valueOf(heights.get(y).get(x));
        Point point = new Point(x, y);
        if (point.equals(lowPoint)) {
          System.out.print(ANSI_BLUE + value + ANSI_RESET);
        } else if (points.contains(point)) {
          System.out.print(ANSI_GREEN + value + ANSI_RESET);
        } else {
          System.out.print(ANSI_RED + value + ANSI_RESET);
        }
      }
      System.out.println("");
    }
    LogAndLoad.log("-".repeat(maxX - minX));
  }

  private static Set<Point> findPointsThatFlowDownToPoint(Point point, Optional<TransformType> currentTransform, int value, int maxX, int maxY, List<List<Integer>> heights) {
    Set<Point> points = new HashSet<>();
    points.add(point);
    for (TransformType transformType : TransformType.values()) {
      if (currentTransform.filter(t -> t.getInvalidTransition().equals(transformType)).isPresent()) {
        continue;
      }

      Point transformedPoint = transformType.getTransform().apply(point);
      if (!isValidPoint(transformedPoint, maxX, maxY)) {
        continue;
      }
      int valueAtPoint = heights.get(transformedPoint.getY()).get(transformedPoint.getX());
      if (valueAtPoint == 9) {
        continue;
      }

      if (valueAtPoint < value) {
        continue;
      }

      points.add(transformedPoint);
      points.addAll(findPointsThatFlowDownToPoint(transformedPoint, Optional.of(transformType), valueAtPoint, maxX, maxY, heights));
    }

    return points;
  }


  private static boolean isValidPoint(Point point, int maxX, int maxY) {
    return point.getX() >= 0 && point.getY() >= 0
        && point.getX() < maxX && point.getY() < maxY;
  }


  enum TransformType {
    UP(point -> new Point(point.getX(), point.getY() - 1)),
    DOWN(point -> new Point(point.getX(), point.getY() + 1)),
    LEFT(point -> new Point(point.getX() - 1, point.getY())),
    RIGHT(point -> new Point(point.getX() + 1, point.getY()));

    private final Function<Point, Point> transform;

    TransformType(Function<Point, Point> transform) {
      this.transform = transform;
    }

    public Function<Point, Point> getTransform() {
      return transform;
    }

    public TransformType getInvalidTransition() {
      switch (this) {
        case UP:
          return DOWN;
        case DOWN:
          return UP;
        case LEFT:
          return RIGHT;
        case RIGHT:
          return LEFT;
        default:
          throw new UnsupportedOperationException();
      }
    }
  }

  static class Point {
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

  static class LowPoint {
    private final Point point;
    private final int value;

    public LowPoint(int x, int y, int value) {
      this.point = new Point(x, y);
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public Point getPoint() {
      return point;
    }
  }
}
