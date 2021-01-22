package com.lorcan.advent;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

public class Day9 {
  private static final Long MAGIC_NUMBER = 167829540L;

  public static void main(String[] args) {
    List<Long> numbers = Utils.readFile("day-9-input.txt")
        .stream()
        .map(Long::parseLong)
        .collect(Collectors.toUnmodifiableList());
    partOne(numbers);
    partTwo(numbers);
  }

  private static void partOne(List<Long> numbers) {
    int sumPointer = 0;
    int pointer = 25;
    List<Long> availableSums = calculateSums(numbers.subList(sumPointer, pointer));
    while (true) {
      long number = numbers.get(pointer);
      if (!availableSums.contains(number)) {
        System.out.format("Sum %d was not available in the previous batch of numbers: pointer: %d\n", number, pointer);
        break;
      }

      availableSums = calculateSums(numbers.subList(++sumPointer, ++pointer));
    }
  }

  private static void partTwo(List<Long> numbers) {
    int startingPointer = 0;
    int count = 0;
    long sum = 0;
    long smallest = Long.MAX_VALUE;
    long largest = Long.MIN_VALUE;

    boolean answerFound = false;
    while (!answerFound) {
      int pointer = startingPointer;
      boolean thisPointerComplete = false;
      while (!thisPointerComplete) {
        count++;
        long number = numbers.get(pointer);
        sum += number;
        smallest = Math.min(smallest, number);
        largest = Math.max(largest, number);

        if (sum == MAGIC_NUMBER) {
          answerFound = true;
          thisPointerComplete = true;
        }

        if (sum > MAGIC_NUMBER) {
          thisPointerComplete = true;
        }
        pointer++;
      }

      startingPointer++;

      if (!answerFound) {
        count = 0;
        sum = 0;
        smallest = Long.MAX_VALUE;
        largest = Long.MIN_VALUE;
      }
    }

    System.out.format("Found %d numbers which sum to %d: smallest: %d, largest: %d, sum of those: %d\n",
      count, MAGIC_NUMBER, smallest, largest, smallest + largest
    );
  }

  private static List<Long> calculateSums(List<Long> numbers) {
    ImmutableList.Builder<Long> builder = ImmutableList.builder();

    for (int i = 0; i < numbers.size() - 1; i++) {
      for (int j = 1; j < numbers.size(); j++) {
        builder.add(numbers.get(i) + numbers.get(j));
      }
    }

    return builder.build();
  }
}
