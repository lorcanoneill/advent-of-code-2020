package com.lorcan.advent.twentytwenty;

import java.util.List;

import com.lorcan.advent.utils.LogAndLoad;

public class Day3 {

  public static void main(String[] args) {
    List<String> lines = LogAndLoad.readFile("twentytwenty/day-3-input.txt");
    SquareType[][] map = buildMap(lines);

    int xBound = lines.get(0).length();
    int yBound = lines.size();

    partOne(map, xBound, yBound);
    partTwo(map, xBound, yBound);
  }

  private static void partOne(SquareType[][] map, int xBound, int yBound) {
    long treesEncountered = traverseMapAndReturnTreesEncountered(map, xBound, yBound, 3, 1);
    System.out.format("Trees encountered: %d", treesEncountered);
  }

  private static void partTwo(SquareType[][] map, int xBound, int yBound) {
    long solution1 = traverseMapAndReturnTreesEncountered(map, xBound, yBound, 1, 1);
    long solution2 = traverseMapAndReturnTreesEncountered(map, xBound, yBound, 3, 1);
    long solution3 = traverseMapAndReturnTreesEncountered(map, xBound, yBound, 5, 1);
    long solution4 = traverseMapAndReturnTreesEncountered(map, xBound, yBound, 7, 1);
    long solution5 = traverseMapAndReturnTreesEncountered(map, xBound, yBound, 1, 2);

    System.out.format("Found the following solutions: %d, %d, %d, %d, %d: product: %d\n",
        solution1, solution2, solution3, solution4, solution5, (solution1 * solution2 * solution3 * solution4 * solution5)
    );
  }

  private static long traverseMapAndReturnTreesEncountered(SquareType[][] map, int xBound, int yBound, int xSlope, int ySlope) {
    int xPointer = 0;
    int yPointer = 0;
    int treesEncountered = 0;

    System.out.format("Bounds of map: 0, 0 -> %d, %d, xSlope: %d, ySlope: %d\n", xBound, yBound, xSlope, ySlope);

    while (yPointer < yBound) {

      if (xPointer + xSlope >= xBound) {
        xPointer = (xPointer + xSlope - xBound);
      } else {
        xPointer += xSlope;
      }

      yPointer += ySlope;

      if (yPointer >= yBound) {
        break;
      }

      SquareType squareType = map[xPointer][yPointer];
      if (squareType == SquareType.TREE) {
        treesEncountered++;
      }
      System.out.format("Current coordinates: x %d, y %d, square: %s, trees: %d\n", xPointer, yPointer, squareType.name(), treesEncountered);
    }

    return treesEncountered;
  }

  private static SquareType[][] buildMap(List<String> lines) {
    SquareType[][] map = new SquareType[lines.get(0).length()][lines.size()];
    int yCoordinate = 0;
    for (String line : lines) {
      int xCoordinate = 0;
      for (char c : line.toCharArray()) {
        map[xCoordinate][yCoordinate] = SquareType.fromChar(c);
        xCoordinate++;
      }
      yCoordinate++;
    }

    return map;
  }

  enum SquareType {
    OPEN,
    TREE;

    public static SquareType fromChar(char c) {
      if (c == '.') {
        return SquareType.OPEN;
      }

      return SquareType.TREE;
    }
  }
}
