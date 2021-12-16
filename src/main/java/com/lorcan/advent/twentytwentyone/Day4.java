package com.lorcan.advent.twentytwentyone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;
import com.lorcan.advent.utils.LogAndLoad;

public class Day4 {

  private static final String INPUT = "twentytwentyone/day-4-input.txt";
  public static void main(String[] args) {
    List<String> lines = LogAndLoad.readFile(INPUT);

    List<Integer> numbers = Arrays.stream(lines.get(0).split(",")).map(Integer::parseInt).collect(Collectors.toUnmodifiableList());

    lines = lines.subList(2, lines.size());
    List<Board> boards = new ArrayList<>();
    for (int i = 0; i < lines.size(); i = i + 6) {
      int[][] cells = new int[5][5];
      for (int j = 0; j < 5; j++) {
        List<Integer> numbersOnLine = Arrays.stream(lines.get(i + j).split("\\s+"))
            .filter(str -> !str.isBlank())
            .map(Integer::parseInt)
            .collect(Collectors.toUnmodifiableList());
        for (int k = 0; k < 5; k++) {
          cells[j][k] = numbersOnLine.get(k);
        }
      }

      boards.add(new Board(cells));
    }

    partOne(numbers, boards);

    partTwo(numbers, boards);
  }

  private static void partOne(List<Integer> numbers, List<Board> boards) {
    for (int i = 0; i < numbers.size(); i++) {
      List<Integer> numbersDrawn = numbers.subList(0, i + 1);
      Optional<Board> maybeBoard = boards.stream().filter(board -> board.hasWinningLine(numbersDrawn))
          .findFirst();

      if (maybeBoard.isPresent()) {
        int lastNumberCalled = Iterables.getLast(numbersDrawn);
        int sumUnmarkedNumbers = maybeBoard.get().sumUnmarkedNumbers(numbersDrawn);

        LogAndLoad.log("part one: last number called: %d, sum of unmarked numbers: %d, product: %d", lastNumberCalled, sumUnmarkedNumbers, lastNumberCalled * sumUnmarkedNumbers);
        return;
      }
    }
  }

  private static void partTwo(List<Integer> numbers, List<Board> boards) {
    for (int i = 0; i < numbers.size(); i++) {
      List<Integer> numbersDrawn = numbers.subList(0, i + 1);
      List<Board> winningBoards = boards.stream().filter(board -> board.hasWinningLine(numbersDrawn))
          .collect(Collectors.toUnmodifiableList());

      if (boards.size() - winningBoards.size() == 0) {
        Board board = Iterables.getLast(winningBoards);

        int lastNumberCalled = Iterables.getLast(numbersDrawn);
        int sumUnmarkedNumbers = board.sumUnmarkedNumbers(numbersDrawn);

        LogAndLoad.log("part two: last number called: %d, sum of unmarked numbers: %d, product: %d", lastNumberCalled, sumUnmarkedNumbers, lastNumberCalled * sumUnmarkedNumbers);
        return;
      } else {
        boards.removeAll(winningBoards);
      }
    }
  }

  static class Board {
    private final int[][] cells;

    public Board(int[][] cells) {
      this.cells = cells;
    }

    public int sumUnmarkedNumbers(List<Integer> numbers) {
      int sum = 0;
      for (int i = 0; i < cells.length; i++) {
        for (int j = 0; j < cells.length; j++) {
          int number = cells[i][j];
          if (!numbers.contains(number)) {
            sum += number;
          }
        }
      }

      return sum;
    }

    public boolean hasWinningLine(List<Integer> numbers) {
      for (int i = 0; i < cells.length; i++) {
        for (int j = 0; j < cells.length; j++) {
          if (!numbers.contains(cells[i][j])) {
            break;
          }

          if (j == cells.length - 1) {
            return true;
          }
        }
      }

      for (int i = 0; i < cells.length; i++) {
        for (int j = 0; j < cells.length; j++) {
          if (!numbers.contains(cells[j][i])) {
            break;
          }

          if (j == cells.length - 1) {
            return true;
          }
        }
      }

      return false;
    }
  }
}
