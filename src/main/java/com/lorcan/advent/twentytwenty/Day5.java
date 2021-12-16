package com.lorcan.advent.twentytwenty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.lorcan.advent.utils.LogAndLoad;

public class Day5 {

  public static void main(String[] args) {
    List<String> boardingPasses = LogAndLoad.readFile("twentytwenty/day-5-input.txt");
    partOne(boardingPasses);
    partTwo(boardingPasses);
  }

  private static void partOne(List<String> boardingPasses) {
    int maxSeatId = -1;
    for (String boardingPass : boardingPasses) {
      int row = findRow(boardingPass);
      int seat = findSeat(boardingPass);
      int seatId = (row * 8) + seat;
      System.out.format("Found row %d, seat %d, seatId %d for boarding pass %s\n", row, seat, seatId, boardingPass);
      maxSeatId = Math.max(maxSeatId, seatId);
    }
    System.out.format("Max seat id: %d\n", maxSeatId);
  }

  private static void partTwo(List<String> boardingPasses) {
    List<Integer> seatIds = new ArrayList<>();
    for (String boardingPass : boardingPasses) {
      int row = findRow(boardingPass);
      int seat = findSeat(boardingPass);
      int seatId = (row * 8) + seat;
      seatIds.add(seatId);
    }

    seatIds.sort(Comparator.naturalOrder());

    for (int seatId : seatIds) {
      if (!seatIds.contains(seatId + 1)) {
        System.out.format("Seat ID %d is not present but seat ID %d below it is\n", seatId + 1, seatId);
      }

      if (!seatIds.contains(seatId - 1)) {
        System.out.format("Seat ID %d is not present but seat ID %d above it is\n", seatId - 1, seatId);
      }


    }
  }

  private static int findRow(String boardingPass) {
    int pointer = 0;
    int row = 0;
    while (pointer < 7) {
      char c = boardingPass.charAt(pointer);
      if (c == 'B') {
        row += Double.valueOf(Math.pow(2, 6 - pointer)).intValue();
      }
      pointer++;
    }
    return row;
  }

  private static int findSeat(String boardingPass) {
    int seat = 0;
    char range = boardingPass.charAt(7);
    if (range == 'R') {
      seat += 4;
    }

    range = boardingPass.charAt(8);
    if (range == 'R') {
      seat += 2;
    }

    range = boardingPass.charAt(9);
    if (range == 'R') {
      seat++;
    }

    return seat;
  }
}
