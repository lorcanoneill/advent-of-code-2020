package com.lorcan.advent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Day12 {
  private static final List<Action> DIRECTIONS = List.of(
      Action.EAST,
      Action.SOUTH,
      Action.WEST,
      Action.NORTH
  );

  private static final Map<Action, Function<Integer, Integer>> X_CHANGES = Map.of(
      Action.EAST, i -> i,
      Action.WEST, i -> -i
  );

  private static final Map<Action, Function<Integer, Integer>> Y_CHANGES = Map.of(
      Action.NORTH, i -> i,
      Action.SOUTH, i -> -i
  );

  public static void main(String[] args) {
    List<Instruction> instructions = Utils.readFile("day-12-input.txt")
        .stream()
        .map(Instruction::fromString)
        .collect(Collectors.toUnmodifiableList());
    partOne(instructions);
    partTwo(instructions);
  }

  private static void partOne(List<Instruction> instructions) {
    int x = 0;
    int y = 0;
    int directionPointer = 0;

    for (Instruction instruction : instructions) {
      Action direction = DIRECTIONS.get(directionPointer);
      switch (instruction.getAction()) {
        case FORWARD:
          if (X_CHANGES.containsKey(direction)) {
            x += X_CHANGES.get(direction).apply(instruction.getNum());
          } else {
            y += Y_CHANGES.get(direction).apply(instruction.getNum());
          }
          break;
        case NORTH:
        case SOUTH:
          y += Y_CHANGES.get(instruction.getAction()).apply(instruction.getNum());
          break;
        case EAST:
        case WEST:
          x += X_CHANGES.get(instruction.getAction()).apply(instruction.getNum());
          break;
        case LEFT:
        case RIGHT:
          directionPointer = changeDirection(instruction.getAction(), instruction.getNum(), directionPointer);
          break;
      }
    }

    System.out.format("After %d instructions, x: %d and y: %d: Manhattan distance: %d\n",
      instructions.size(),
      x,
      y,
      ((long) Math.abs(x) + Math.abs(y))
    );
  }

  private static void partTwo(List<Instruction> instructions) {
    int x = 0;
    int y = 0;
    int waypointX = 10;
    int waypointY = 1;

    for (Instruction instruction : instructions) {
      int xDifference = (waypointX - x);
      int yDifference = (waypointY - y);
      double slope = (xDifference == 0) ? 0 : ((double) (yDifference) /  (double) (xDifference));
      int quadrant = (slope > 0) ? (yDifference > 0 ? 1 : 3) : (xDifference > 0 ? 2 : 4);
      Action instructionAction = instruction.getAction();
      int num = instruction.getNum();

      System.out.format("Ship: x: %d, y: %d | waypoint: x: %d, y: %d | diff: x: %d, y: %d | quadrant: %d, slope: %f | Action: %s %d\n",
          x, y, waypointX, waypointY, xDifference, yDifference, quadrant, slope, instructionAction, num
      );
      switch (instructionAction) {
        case NORTH:
        case SOUTH:
          waypointY += Y_CHANGES.get(instructionAction).apply(num);
          break;
        case EAST:
        case WEST:
          waypointX += X_CHANGES.get(instructionAction).apply(num);
          break;
        /**
         *                    10 x
         *                    9
         *                    8
         *                    7
         *                    6
         *                    4
         *                    3
         *                    2
         * x                  1
         * 10-9-8-7-5-4-3-2-1-0-1-2-3-4-5-6-7-8-9-10
         *                    1                   x
         *                    2
         *                    3
         *                    4
         *                    5
         *                    6
         *                    7
         *                    8
         *                    9
         *                  x 10
         *
         *  starting point is 10,-1
         *  LEFT 90 from here is 1, 10
         *  LEFT 180 from here is -10, 1
         *  LEFT 270 from here is -1, -10
         *  RIGHT 90 from here is -1, -10
         *  RIGHT 180 from here is -10, 1
         *  RIGHT 270 from here is 1, 10
         *
         *  starting point is -1, -10
         *  LEFT 90 is 10, -1
         *  LEFT 180 is 1, 10
         *  LEFT 270 is -10, 1
         *  RIGHT 90 is -10, 1
         *  RIGHT 180 is 1, 10
         *  RIGHT 270 is 10, -1
         *
         *  starting point is -10, 1
         *  LEFT 90 is 1, 10
         *  LEFT 180 is 10, -1
         *  LEFT 270 is -1, 10
         *
         *  starting point is 1, 10
         *  LEFT 90 is -10, 1
         *  LEFT 180 is -1, -10
         *  LEFT 270 is 10, -1
         *  RIGHT 90 is 10, -1
         *  RIGHT 180 is -1, -10
         *  RIGHT 270 is -10, 1
         */
        case LEFT:
        case RIGHT:
          // transform x and y

          if (num == 180) {
            waypointX = x - xDifference;
            waypointY = y - yDifference;
            break;
          }

          boolean isLeftTurnBy90 = (instructionAction == Action.LEFT && num == 90) || (instructionAction == Action.RIGHT && num == 270);
          if (quadrant == 1) { // 10, 1
            if (isLeftTurnBy90) { // -1, 10
              waypointX = x - Math.abs(yDifference);
              waypointY = y + Math.abs(xDifference);
            } else { // 1, -10
              waypointX = x + Math.abs(yDifference);
              waypointY = y - Math.abs(xDifference);
            }
          } else if (quadrant == 2) { // 1, -10
            if (isLeftTurnBy90) { // 10, 1
              waypointX = x + Math.abs(yDifference);
              waypointY = y + Math.abs(xDifference);
            } else { // -10, -1
              waypointX = x - Math.abs(yDifference);
              waypointY = y - Math.abs(xDifference);
            }
          } else if (quadrant == 3) {  // -10, -1
            if (isLeftTurnBy90) { // 1, -10
              waypointX = x + Math.abs(yDifference);
              waypointY = y - Math.abs(xDifference);
            } else { // -1, 10
              waypointX = x - Math.abs(yDifference);
              waypointY = y + Math.abs(xDifference);
            }
          } else { // -1, 10
            if (isLeftTurnBy90) { // -10, -1
              waypointX = x - Math.abs(yDifference);
              waypointY = y - Math.abs(xDifference);
            } else { // // 10, 1
              waypointX = x + Math.abs(yDifference);
              waypointY = y + Math.abs(xDifference);
            }
          }
          break;
        case FORWARD:
          // from 0, 0 to 10, 1 (10 times) gets you 100, 10
          // 0, 0 -> 10, 1 -> 20, 2 -> 30, 3
          // from 100, 10 to 110, 114 (7 times) for a total change of 70, 28 gets you 170, 38

          // calculate the slope formula N times
          // 0, 0 - slope is 0.1
          // 0 - a/0 - b = 0.1
          // 0 - a =  (0.1) * (0 - b)
          // -10a = -b;
          // 10a = b;
          // 10, 1 - slope is 0.1
          // 10 - a/1 - b = 0.1
          // 10 - a = (0.1) * (1 - b)
          // 100 - 10a = 1 - b
          //

//          int newX = x;
//          int newY = y;
//          int xDistance = (waypointX - x);
//          int yDistance = (waypointY - y);
          x += (num) * xDifference;
          y += (num) * yDifference;
          waypointX = x + xDifference;
          waypointY = y + yDifference;
//
//          for (int n = 1; n <= num; n++) {
//            newX += xDistance;
//            newY += yDistance;
//            waypointX = newX + xDistance;
//            waypointY = newY + yDistance;
//          }
//          x = newX;
//          y = newY;
          break;
      }
    }

    System.out.format("After %d instructions, x: %d and y: %d: Manhattan distance: %d\n",
        instructions.size(),
        x,
        y,
        ((long) Math.abs(x) + Math.abs(y))
    );
  }

  private static int changeDirection(Action action, int num, int existingDirectionPointer) {
    switch (action) {
      case LEFT:
        switch (num) {
          case 90:
            switch (existingDirectionPointer) {
              case 0:
                return 3;
              case 1:
                return 0;
              case 2:
                return 1;
              case 3:
                return 2;
            }
          case 180:
            switch (existingDirectionPointer) {
              case 0:
                return 2;
              case 1:
                return 3;
              case 2:
                return 0;
              case 3:
                return 1;
            }
          case 270:
            switch (existingDirectionPointer) {
              case 0:
                return 1;
              case 1:
                return 2;
              case 2:
                return 3;
              case 3:
                return 0;
            }
        }
      case RIGHT:
        switch (num) {
          case 90:
            switch (existingDirectionPointer) {
              case 0:
                return 1;
              case 1:
                return 2;
              case 2:
                return 3;
              case 3:
                return 0;
            }
          case 180:
            switch (existingDirectionPointer) {
              case 0:
                return 2;
              case 1:
                return 3;
              case 2:
                return 0;
              case 3:
                return 1;
            }
          case 270:
            switch (existingDirectionPointer) {
              case 0:
                return 3;
              case 1:
                return 0;
              case 2:
                return 1;
              case 3:
                return 2;
            }
        }
    }

    throw new UnsupportedOperationException("Cannot process action " + action);
  }

  static class Instruction {
    private final Action action;
    private final int num;

    private Instruction(Action action, int num) {
      this.action = action;
      this.num = num;
    }

    public Action getAction() {
      return action;
    }

    public int getNum() {
      return num;
    }

    public static Instruction fromString(String str) {
      return new Instruction(
          Action.of(str.charAt(0)),
          Integer.parseInt(str.substring(1), 10)
      );
    }
  }

  enum Action {
    NORTH('N'),
    SOUTH('S'),
    EAST('E'),
    WEST('W'),
    LEFT('L'),
    RIGHT('R'),
    FORWARD('F');

    private final char c;

    Action(char c) {
      this.c = c;
    }

    public char getC() {
      return c;
    }

    public static Action of(char c) {
      return Arrays.stream(values())
          .filter(action -> action.getC() == c)
          .findAny()
          .orElseThrow();
    }
  }
}
