package com.lorcan.advent.twentytwentyone;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day11 {

  private static final String INPUT = "twentytwentyone/day-11-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-11-sample-input.txt";

  public static void main(String[] args) {
    Octopus[][] sampleGrid = buildGrid(SAMPLE_INPUT);
    partTwo(sampleGrid);
    partOne(sampleGrid);
//
    Octopus[][] grid = buildGrid(INPUT);
    partOne(grid);
    partTwo(grid);
  }

  private static void partOne(Octopus[][] grid) {
    long flashes = 0;
    int maxY = grid.length;
    int maxX = grid[0].length;
    for (int step = 1; step <= 100; step++) {
      int countOfPointsExamined = 0;
      long flashesThisStep = 0;
      Stack<Point> points = calculateBaseStack(maxX, maxY);
      while (!points.isEmpty()) {
        Point point = points.pop();

        if (!isPointValid(point, maxX, maxY)) {
          continue;
        }

        countOfPointsExamined++;

        Octopus octopus = grid[point.getY()][point.getX()].increaseEnergyLevel();
        if (octopus.calculateFlashStatus() == FlashStatus.WILL_FLASH) {
          points.addAll(findAdjacentPoints(octopus.getPoint()));
          flashesThisStep += 1;
          octopus = octopus.setHasFlashedThisTurn();
        }
        grid[point.getY()][point.getX()] = octopus;
      }
      flashes += flashesThisStep;
      LogAndLoad.log("Total flashes: %d, step %d and %d points: %d", flashes, step, countOfPointsExamined, flashesThisStep);

      points = calculateBaseStack(maxX, maxY);

      while (!points.isEmpty()) {
        Point point = points.pop();
        Octopus octopus = grid[point.getY()][point.getX()];
        grid[point.getY()][point.getX()] = octopus.resetOctopus();
      }
    }
  }

  private static void partTwo(Octopus[][] grid) {
    boolean allFlashed = false;
    int maxY = grid.length;
    int maxX = grid[0].length;
    int step = 0;
    while (!allFlashed) {
      step++;
      Stack<Point> points = calculateBaseStack(maxX, maxY);
      while (!points.isEmpty()) {
        Point point = points.pop();

        if (!isPointValid(point, maxX, maxY)) {
          continue;
        }

        Octopus octopus = grid[point.getY()][point.getX()].increaseEnergyLevel();
        if (octopus.calculateFlashStatus() == FlashStatus.WILL_FLASH) {
          points.addAll(findAdjacentPoints(octopus.getPoint()));
          octopus = octopus.setHasFlashedThisTurn();
        }
        grid[point.getY()][point.getX()] = octopus;
      }

      points = calculateBaseStack(maxX, maxY);

      boolean meetsEndCondition = true;
      while (!points.isEmpty()) {
        Point point = points.pop();
        Octopus octopus = grid[point.getY()][point.getX()];
        meetsEndCondition &= octopus.calculateFlashStatus() == FlashStatus.ALREADY_FLASHED;
        grid[point.getY()][point.getX()] = octopus.resetOctopus();
      }

      allFlashed = meetsEndCondition;
    }

    LogAndLoad.log("All octopi flashed on step %d", step);
  }

  private static boolean isPointValid(Point point, int maxX, int maxY) {
    return point.getX() >= 0 && point.getX() < maxX
        && point.getY() >= 0 && point.getY() < maxY;
  }

  private static Stack<Point> calculateBaseStack(int maxX, int maxY) {
    Stack<Point> stack = new Stack<>();
    for (int y = 0; y < maxY; y++) {
      for (int x = 0; x < maxX; x++) {
        stack.add(new Point(x, y));
      }
    }

    return stack;
  }


  private static Octopus[][] buildGrid(String input) {
    List<String> lines = LogAndLoad.readFile(input);
    int maxX = lines.get(0).length();
    int maxY = lines.size();
    Octopus[][] grid = new Octopus[maxY][maxX];
    for (int y = 0; y < maxY; y++) {
      String line = lines.get(y);
      for (int x = 0; x < maxX; x++) {
        grid[y][x] = new Octopus(new Point(x, y), Integer.parseInt(line.substring(x, x + 1)));
      }
    }

    return grid;
  }

  private static Set<Point> findAdjacentPoints(Point point) {
    return Arrays.stream(Direction.values())
        .map(direction -> direction.getTransform().apply(point))
        .collect(Collectors.toUnmodifiableSet());
  }

  static class Octopus {
    private final Point point;
    private int energyLevel;
    boolean hasFlashedThisTurn;

    public Octopus(Point point, int energyLevel) {
      this.point = point;
      this.energyLevel = energyLevel;
    }

    public Point getPoint() {
      return point;
    }

    public Octopus increaseEnergyLevel() {
      if (energyLevel <= 9) {
        this.energyLevel = energyLevel + 1;
      }
      return this;
    }

    public Octopus setHasFlashedThisTurn() {
      this.hasFlashedThisTurn = true;
      return this;
    }

    public FlashStatus calculateFlashStatus() {
      if (hasFlashedThisTurn) {
        return FlashStatus.ALREADY_FLASHED;
      }

      if (energyLevel > 9) {
        return FlashStatus.WILL_FLASH;
      }

      return FlashStatus.WILL_NOT_FLASH;
    }

    public Octopus resetOctopus() {
      if (this.energyLevel > 9) {
        this.energyLevel = 0;
      }

      this.hasFlashedThisTurn = false;
      return this;
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
  }

  enum FlashStatus {
    WILL_FLASH,
    WILL_NOT_FLASH,
    ALREADY_FLASHED;
  }

  enum Direction {
    UP(point -> new Point(point.getX(), point.getY() - 1)),
    DOWN(point -> new Point(point.getX(), point.getY() + 1)),
    LEFT(point -> new Point(point.getX() - 1, point.getY())),
    RIGHT(point -> new Point(point.getX() + 1, point.getY())),
    UP_LEFT(point -> new Point(point.getX() - 1, point.getY() - 1)),
    UP_RIGHT(point -> new Point(point.getX() + 1, point.getY() - 1)),
    DOWN_LEFT(point -> new Point(point.getX() - 1, point.getY() + 1)),
    DOWN_RIGHT(point -> new Point(point.getX() + 1, point.getY() + 1));

    private final Function<Point, Point> transform;

    Direction(Function<Point, Point> transform) {
      this.transform = transform;
    }

    public Function<Point, Point> getTransform() {
      return transform;
    }
  }
}
