package com.lorcan.advent.twentytwentyone;

import java.util.List;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day2 {

  private static final String INPUT = "twentytwentyone/day-2-input.txt";
  public static void main(String[] args) {
    List<Command> commands = LogAndLoad.readFile(INPUT)
        .stream()
        .map(Command::from)
        .collect(Collectors.toUnmodifiableList());

    partOne(commands);
    partTwo(commands);
  }

  private static void partOne(List<Command> commands) {
    int horizontalPosition = 0;
    int depth = 0;

    for (Command command : commands) {
      switch (command.getDirection()) {
        case FORWARD:
          horizontalPosition += command.getUnits();
          break;
        case DOWN:
          depth += command.getUnits();
          break;
        case UP:
          depth -= command.getUnits();
          break;
      }
    }

    LogAndLoad.log("Part one: horiztontal posiition: %d, depth: %d, product: %d", horizontalPosition, depth, horizontalPosition * depth);
  }

  private static void partTwo(List<Command> commands) {
    int horizontalPosition = 0;
    int depth = 0;
    int aim = 0;

    for (Command command : commands) {
      switch (command.getDirection()) {
        case FORWARD:
          horizontalPosition += command.getUnits();
          depth += (aim * command.getUnits());
          break;
        case DOWN:
          aim += command.getUnits();
          break;
        case UP:
          aim -= command.getUnits();
          break;
      }
    }

    LogAndLoad.log("Part two: aim: %d, horiztontal posiition: %d, depth: %d, product: %d", aim, horizontalPosition, depth, horizontalPosition * depth);
  }

  static class Command {
    private final Direction direction;
    private final int units;

    public Command(Direction direction, int units) {
      this.direction = direction;
      this.units = units;
    }

    public Direction getDirection() {
      return direction;
    }

    public int getUnits() {
      return units;
    }

    public static Command from(String line) {
      String[] arr = line.split(" ");
      return new Command(
          Direction.from(arr[0]),
          Integer.parseInt(arr[1])
      );
    }
  }

  enum Direction {
    UP,
    FORWARD,
    DOWN;

    public static Direction from(String str) {
      return Direction.valueOf(str.toUpperCase());
    }
  }
}
