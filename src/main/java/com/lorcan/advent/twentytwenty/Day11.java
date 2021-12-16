package com.lorcan.advent.twentytwenty;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.lorcan.advent.utils.LogAndLoad;

public class Day11 {

  public static void main(String[] args) {
    List<String> lines = LogAndLoad.readFile("twentytwenty/day-11-input.txt");

    int xBound = lines.get(0).length();
    int yBound = lines.size();

    PositionType[][] grid = new PositionType[xBound][yBound];

    for (int y = 0; y < yBound; y++) {
      String line = lines.get(y);
      for (int x = 0; x < xBound; x++) {
        grid[x][y] = PositionType.of(line.charAt(x));
      }
    }

    partOne(grid, xBound, yBound);
    partTwo(grid, xBound, yBound);
  }

  private static void partOne(PositionType[][] grid, int xBound, int yBound) {
    int iterations = 0;
    while (true) {
      iterations++;
      PositionType[][] transformedGrid = transformGrid(grid, xBound, yBound);
      if (gridsAreEqual(grid, transformedGrid, xBound, yBound)) {
        break;
      } else {
        grid = transformedGrid;
      }
    }

    System.out.format("Seats occupied after %d iterations: %d\n", iterations, countOccupiedSeats(grid, xBound, yBound));
  }

  private static void partTwo(PositionType[][] grid, int xBound, int yBound) {
    int iterations = 0;
    while (true) {
      iterations++;
      PositionType[][] transformedGrid = transformGridBasedOnView(grid, xBound, yBound);
      if (gridsAreEqual(grid, transformedGrid, xBound, yBound)) {
        break;
      } else {
        grid = transformedGrid;
      }
    }

    System.out.format("Seats occupied after %d iterations: %d\n", iterations, countOccupiedSeats(grid, xBound, yBound));
  }

  private static void printGrid(PositionType[][] grid, int xBound, int yBound, String prefix) {
    System.out.println(prefix);
    for (int y = 0; y < yBound; y++) {
      for (int x = 0; x < xBound; x++) {
        System.out.print(grid[x][y].getC());
      }
      System.out.print("\n");
    }

    System.out.println();
  }

  private static int countOccupiedSeats(PositionType[][] grid, int xBound, int yBound) {
    int occupied = 0;
    for (int x = 0; x < xBound; x++) {
      for (int y = 0; y < yBound; y++) {
        if (grid[x][y] == PositionType.OCCUPIED) {
          occupied++;
        }
      }
    }

    return occupied;
  }

  private static PositionType[][] transformGrid(PositionType[][] grid, int xBound, int yBound) {
    PositionType[][] transformedGrid = new PositionType[xBound][yBound];
    for (int x = 0; x < xBound; x++) {
      for (int y = 0; y < yBound; y++) {
        PositionType positionType = grid[x][y];
        PositionType transformedPositionType = positionType;
        if (positionType != PositionType.FLOOR) {
          List<Position> adjacentPositions = findAdjacentPositions(grid, x, y, xBound, yBound);
          if (positionType == PositionType.EMPTY) {
            if (adjacentPositions.stream().noneMatch(position -> position.getPositionType() == PositionType.OCCUPIED)) {
              transformedPositionType = PositionType.OCCUPIED;
            }
          } else {
            if (adjacentPositions.stream().filter(position -> position.getPositionType() == PositionType.OCCUPIED).count() >= 4) {
              transformedPositionType = PositionType.EMPTY;
            }
          }
        }

        transformedGrid[x][y] = transformedPositionType;
      }
    }

    return transformedGrid;
  }

  private static PositionType[][] transformGridBasedOnView(PositionType[][] grid, int xBound, int yBound) {
    PositionType[][] transformedGrid = new PositionType[xBound][yBound];
    for (int x = 0; x < xBound; x++) {
      for (int y = 0; y < yBound; y++) {
        PositionType positionType = grid[x][y];
        PositionType transformedPositionType = positionType;
        if (positionType != PositionType.FLOOR) {
          List<PositionType> viewablePositionTypes = findAllViews(grid, x, y, xBound, yBound);
          if (positionType == PositionType.EMPTY) {
            if (viewablePositionTypes.stream().noneMatch(position -> position == PositionType.OCCUPIED)) {
              transformedPositionType = PositionType.OCCUPIED;
            }
          } else {
            if (viewablePositionTypes.stream().filter(position -> position == PositionType.OCCUPIED).count() >= 5) {
              transformedPositionType = PositionType.EMPTY;
            }
          }
        }

        transformedGrid[x][y] = transformedPositionType;
      }
    }

    return transformedGrid;
  }

  private static boolean gridsAreEqual(PositionType[][] a, PositionType[][] b, int xBound, int yBound) {
    for (int x = 0; x < xBound; x++) {
      for (int y = 0; y < yBound; y++) {
        if (a[x][y] != b[x][y]) {
          return false;
        }
      }
    }

    return true;
  }

  private static List<Position> findAdjacentPositions(PositionType[][] grid, int x, int y, int xBound, int yBound) {
    ImmutableList.Builder<Position> builder = ImmutableList.builder();
    buildAdjacentCoordinates(x, y)
        .forEach(coordinates -> {
          if (isValidCoordinate(coordinates, xBound, yBound)) {
            builder.add(new Position(coordinates.getX(), coordinates.getY(), grid[coordinates.getX()][coordinates.getY()]));
          }
        });
    return builder.build();
  }

  private static List<PositionType> findAllViews(PositionType[][] grid, int x, int y, int xBound, int yBound) {
    return List.of(
        findViewInDirection(grid, x, y, xBound, yBound, i -> i - 1, i -> i - 1),
        findViewInDirection(grid, x, y, xBound, yBound, i -> i, i -> i - 1),
        findViewInDirection(grid, x, y, xBound, yBound, i -> i + 1, i -> i - 1),
        findViewInDirection(grid, x, y, xBound, yBound, i -> i - 1, i -> i),
        findViewInDirection(grid, x, y, xBound, yBound, i -> i + 1, i -> i),
        findViewInDirection(grid, x, y, xBound, yBound, i -> i - 1, i -> i + 1),
        findViewInDirection(grid, x, y, xBound, yBound, i -> i, i -> i + 1),
        findViewInDirection(grid, x, y, xBound, yBound, i -> i + 1, i -> i + 1)
    ).stream()
        .filter(p -> p != PositionType.FLOOR)
        .collect(Collectors.toUnmodifiableList());
  }

  private static PositionType findViewInDirection(PositionType[][] grid, int x, int y, int xBound, int yBound, Function<Integer, Integer> xChange, Function<Integer, Integer> yChange) {
    PositionType positionType = PositionType.FLOOR;
    x = xChange.apply(x);
    y = yChange.apply(y);
    while ((x >= 0 && x < xBound) && (y >= 0 && y < yBound)) {
      positionType = grid[x][y];
      if (positionType != PositionType.FLOOR) {
        break;
      }

      x = xChange.apply(x);
      y = yChange.apply(y);
    }

    return positionType;
  }

  private static boolean isValidCoordinate(Coordinates coordinates, int xBound, int yBound) {
    return coordinates.getX() >= 0 & coordinates.getX() < xBound & coordinates.getY() >= 0 & coordinates.getY() < yBound;
  }

  private static List<Coordinates> buildAdjacentCoordinates(int x, int y) {
    return List.of(
        Coordinates.of(x - 1, y - 1),
        Coordinates.of(x, y - 1),
        Coordinates.of(x + 1, y - 1),
        Coordinates.of(x - 1, y),
        Coordinates.of(x + 1, y),
        Coordinates.of(x - 1, y + 1),
        Coordinates.of(x, y + 1),
        Coordinates.of(x + 1, y + 1)
    );
  }

  static class Coordinates {
    private final int x;
    private final int y;

    Coordinates(int x, int y) {
      this.x = x;
      this.y = y;
    }

    public static Coordinates of(int x, int y) {
      return new Coordinates(x, y);
    }

    public int getY() {
      return y;
    }

    public int getX() {
      return x;
    }
  }


  static class Position {
    private final int x;
    private final int y;
    private final PositionType positionType;

    private Position(int x, int y, PositionType positionType) {
      this.x = x;
      this.y = y;
      this.positionType = positionType;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public PositionType getPositionType() {
      return positionType;
    }
  }

  enum PositionType {
    FLOOR('.'),
    EMPTY('L'),
    OCCUPIED('#');

    private final char c;

    PositionType(char c) {
      this.c = c;
    }

    public char getC() {
      return c;
    }

    public static PositionType of(char c) {
      switch (c) {
        case 'L':
          return EMPTY;
        case '.':
          return FLOOR;
        case '#':
          return OCCUPIED;
        default:
          throw new UnsupportedOperationException("Cannot build PositionType from " + c);
      }
    }
  }
}
