package com.lorcan.advent.twentytwentyone;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import com.lorcan.advent.utils.LogAndLoad;
import com.lorcan.advent.utils.LogAndLoad.BackgroundColour;
import com.lorcan.advent.utils.LogAndLoad.TextColour;

public class Day13 {

  private static final String INPUT = "twentytwentyone/day-13-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-13-sample-input.txt";

  public static void main(String[] args) {
    List<String> lines = LogAndLoad.readFile(INPUT);
    List<Point> points = new ArrayList<>();
    List<Fold> folds = new ArrayList<>();
    for (String line : lines) {
      if (line.isBlank()) {
        continue;
      }

      if (line.startsWith("fold along ")) {
        line = line.replaceAll("fold along ", "");
        folds.add(Fold.fromString(line));
      } else {
        points.add(Point.fromString(line));
      }
    }

    boolean[][] dotMatrix = buildDotMatrix(points);
    partOne(dotMatrix, folds);
    partTwo(dotMatrix, folds);
  }

  private static void partOne(boolean[][] dotMatrix, List<Fold> folds) {
    Fold fold = folds.get(0);
    boolean[][] foldedMatrix = fold(dotMatrix, fold);
    int dots = countDots(foldedMatrix);
    LogAndLoad.log("Found %d dots after fold %s", dots, fold);

    fold = folds.get(1);
    foldedMatrix = fold(foldedMatrix, folds.get(1));
    dots = countDots(foldedMatrix);
    LogAndLoad.log("Found %d dots after fold %s", dots, fold);
  }

  private static void partTwo(boolean[][] dotMatrix, List<Fold> folds) {
    for (Fold fold : folds) {
      dotMatrix = fold(dotMatrix, fold);
    }

    LogAndLoad.log("Printing completed matrix");
    printMatrix(dotMatrix);
  }

  private static void printMatrix(boolean[][] dotMatrix) {
    int maxX = dotMatrix[0].length;
    System.out.println("*".repeat(maxX * 2));

    for (boolean[] matrix : dotMatrix) {
      for (int x = 0; x < maxX; x++) {
        if (matrix[x]) {
          LogAndLoad.logWithoutNewLine(TextColour.BLACK, BackgroundColour.RED, "@@");
        } else {
          LogAndLoad.logWithoutNewLine(TextColour.WHITE, BackgroundColour.WHITE, "@@");
        }
      }
      System.out.println();
    }

    System.out.println("-".repeat(maxX * 2));

    // C B L V A R F H
    // CBLVARFH - incorrect
    // CBLVQRFH - incorrect
    // CBLVGRFH - incorrect
    // CBLUGRFH - incorrect
    // CBLVQRFM - incorrect
    // CBLUQRFM - incorrect
    // CBLUORFH -
    // COLUDRFH - incorrect
  }

  private static int countDots(boolean[][] dotMatrix) {
    int sum = 0;
    int maxX = dotMatrix[0].length;
    for (boolean[] matrix : dotMatrix) {
      for (int x = 0; x < maxX; x++) {
        if (matrix[x]) {
          sum++;
        }
      }
    }

    return sum;
  }

  private static boolean[][] fold(boolean[][] dotMatrix, Fold fold) {
    int foldAlong = fold.getLines();
    int existingYLength = dotMatrix.length;
    int existingXLength = dotMatrix[0].length;
    LogAndLoad.log("Existing matrix: x: 0->%d, y: 0->%d", existingXLength - 1, existingYLength - 1);
    final boolean[][] newMatrix;
    switch (fold.getFoldType()) {
      case Y:
        int startingY = existingYLength - 1;
        LogAndLoad.log("Folding %s; starting from %d", fold, startingY);
        // fold vertically
        newMatrix = new boolean[foldAlong][existingXLength];
        // work back from bottom most element
        for (int x = 0; x < existingXLength; x++) {
          for (int y = startingY; y > foldAlong; y--) {
            int yElementToSet = startingY - y;
            boolean isDot = dotMatrix[yElementToSet][x] || dotMatrix[y][x];
            newMatrix[yElementToSet][x] = isDot;
            if (isDot) {
              LogAndLoad.log("Found dot by applying fold %s: y: %d onto y: %d at x: %d", fold, y, yElementToSet, x);
            }
          }
        }
        break;
      case X:
        int startingX = existingXLength - 1;
        LogAndLoad.log("Folding %s; starting from %d", fold, startingX);
        // fold horizontally
        newMatrix = new boolean[existingYLength][foldAlong];
        // work back from the furthest right element
        for (int y = 0; y < existingYLength; y++) {
          for (int x = startingX; x > foldAlong; x--) {
            int xElementToSet = startingX - x;
            boolean isDot = dotMatrix[y][xElementToSet] || dotMatrix[y][x];
            newMatrix[y][xElementToSet] = isDot;
            if (isDot) {
              LogAndLoad.log("Found dot by applying fold %s: x: %d onto x: %d at y: %d", fold, x, xElementToSet, y);
            }
          }
        }
        break;
      default:
        throw new UnsupportedOperationException();
    }

    return newMatrix;
  }

  private static boolean[][] buildDotMatrix(List<Point> points) {
    int maxX = points.stream()
        .map(Point::getX)
        .max(Comparator.naturalOrder())
        .get();

    int maxY = points.stream()
        .map(Point::getY)
        .max(Comparator.naturalOrder())
        .get();

    boolean[][] matrix = new boolean[maxY + 1][maxX + 1];

    for (int y = 0; y <= maxY; y++) {
      for (int x = 0; x <= maxX; x++) {
        final int finalX = x;
        final int finalY = y;
        matrix[y][x] = points.stream()
            .anyMatch(point -> point.getX() == finalX && point.getY() == finalY);
      }
    }
    return matrix;
  }

  enum FoldType {
    X,
    Y;

    public static FoldType fromString(String str) {
      return FoldType.valueOf(str.toUpperCase());
    }
  };

  static class Fold {
    private final FoldType foldType;
    private final int lines;

    public Fold(FoldType foldType, int lines) {
      this.foldType = foldType;
      this.lines = lines;
    }

    public FoldType getFoldType() {
      return foldType;
    }

    public int getLines() {
      return lines;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Fold fold = (Fold) o;
      return lines == fold.lines && foldType == fold.foldType;
    }

    @Override
    public int hashCode() {
      return Objects.hash(foldType, lines);
    }

    public static Fold fromString(String str) {
      String[] arr = str.split("=");
      return new Fold(FoldType.fromString(arr[0]), Integer.parseInt(arr[1]));
    }

    @Override
    public String toString() {
      return String.format("%s -> %d", foldType, lines);
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
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Point point = (Point) o;
      return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y);
    }
    public static Point fromString(String str) {
      String[] arr = str.split(",");
      return new Point(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]));
    }
  }
}
