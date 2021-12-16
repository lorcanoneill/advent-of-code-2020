package com.lorcan.advent.twentytwenty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.google.common.base.Stopwatch;
import com.lorcan.advent.utils.LogAndLoad;

public class Day15 {
  public static void main(String[] args) {
    List<Integer> startingNumbers =
        Arrays.stream(
          LogAndLoad.readFileAsString("twentytwenty/day-15-input.txt").split(",")
        )
        .map(Integer::parseInt)
        .collect(Collectors.toUnmodifiableList());
    partOne(startingNumbers);
  }

  private static void partOne(List<Integer> startingNumbers) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    int pointer = 1;
    int numberSaid = -1;
    Map<Integer, Integer> mostRecentTurnMap = new HashMap<>();
    Map<Integer, Integer> lessRecentTurnMap = new HashMap<>();

    long finishingTurn = 30_000_000L;
//    int finishingTurn = 2020;
    while (pointer <= finishingTurn) {
      if (pointer <= startingNumbers.size()) {
        numberSaid = startingNumbers.get(pointer - 1);
      } else {
        if (!mostRecentTurnMap.containsKey(numberSaid)) {
          throw new UnsupportedOperationException("Number said " + numberSaid + " was not in most recent turn map");
        }

        int mostRecentTurn = mostRecentTurnMap.get(numberSaid);
        int lessRecentTurn = lessRecentTurnMap.getOrDefault(numberSaid, -1);

        if (lessRecentTurn == -1) {
          numberSaid = 0;
        } else {
          numberSaid = mostRecentTurn - lessRecentTurn;
        }
      }

      if (mostRecentTurnMap.containsKey(numberSaid)) {
        lessRecentTurnMap.put(numberSaid, mostRecentTurnMap.get(numberSaid));
      }

      mostRecentTurnMap.put(numberSaid, pointer);

      if (pointer % 500000 == 0) {
        System.out.format("Number spoken at turn %d after %dms: %d\n", pointer, stopwatch.elapsed(TimeUnit.MILLISECONDS), numberSaid);
      }

      pointer++;
    }

    System.out.format("Number at turn %d after %dms: %d\n", finishingTurn, stopwatch.elapsed(TimeUnit.MILLISECONDS), numberSaid);
  }
}
