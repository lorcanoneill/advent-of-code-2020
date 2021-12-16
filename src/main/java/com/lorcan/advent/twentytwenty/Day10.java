package com.lorcan.advent.twentytwenty;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.lorcan.advent.utils.LogAndLoad;


public class Day10 {

  public static void main(String[] args) {
    List<Integer> adapterJoltageRatings = LogAndLoad.readFile("twentytwenty/day-10-input.txt")
        .stream()
        .map(Integer::parseInt)
        .sorted()
        .collect(Collectors.toUnmodifiableList());

    partOne(adapterJoltageRatings);
    partTwo(adapterJoltageRatings);
  }

  private static void partOne(List<Integer> adaptorJoltageRatings) {
    int pointer = 0;
    int numberOfOneJoltDifferences = 0;
    int numberOfThreeJoltDifferences = 0;
    int previousJoltageRating = 0;
    while (pointer < adaptorJoltageRatings.size()) {
      int joltageRating = adaptorJoltageRatings.get(pointer++);
      int diff = joltageRating - previousJoltageRating;
      if (diff == 1) {
        numberOfOneJoltDifferences++;
      } else if (diff == 3) {
        numberOfThreeJoltDifferences++;
      }

      System.out.format("Found joltageRating of %d, previous: %d, diff: %d\n", joltageRating, previousJoltageRating, diff);
      previousJoltageRating = joltageRating;
    }

    numberOfThreeJoltDifferences++;

    System.out.format("Number of one jolt differences: %d, number of three jolt differences: %d, product: %d\n", numberOfOneJoltDifferences, numberOfThreeJoltDifferences, numberOfOneJoltDifferences * numberOfThreeJoltDifferences);
  }

  private static void partTwo(List<Integer> adaptorJoltageRatings) {
    ImmutableList.Builder<Integer> builder = ImmutableList.builder();
    builder.addAll(adaptorJoltageRatings);
    builder.add(0);
    ImmutableList<Integer> adaptorJoltageRatingsWith0 = builder.build();

    List<Integer> reversedAdaptorJoltageRatings = adaptorJoltageRatingsWith0.stream()
        .sorted(Comparator.reverseOrder())
        .collect(Collectors.toUnmodifiableList());


    Map<Integer, Long> countOfPathsFromJoltageRating = new HashMap<>();
    for (int adaptorJoltageRating : reversedAdaptorJoltageRatings) {
      long count = 0;
      if (countOfPathsFromJoltageRating.containsKey(adaptorJoltageRating + 3)) {
        count += countOfPathsFromJoltageRating.get(adaptorJoltageRating + 3);
      }
      if (countOfPathsFromJoltageRating.containsKey(adaptorJoltageRating + 2)) {
        count += countOfPathsFromJoltageRating.get(adaptorJoltageRating + 2);
      }

      if (countOfPathsFromJoltageRating.containsKey(adaptorJoltageRating + 1)) {
        count += countOfPathsFromJoltageRating.get(adaptorJoltageRating + 1);
      }

      if (count == 0) {
        count = 1;
      }

      countOfPathsFromJoltageRating.put(adaptorJoltageRating, count);
    }



    countOfPathsFromJoltageRating.entrySet()
        .stream().sorted(Collections.reverseOrder(Entry.comparingByKey()))
        .forEach(entry -> System.out.format("Count of paths from %d: %d\n", entry.getKey(), entry.getValue()));

    System.out.format("Count of paths from pointers: %s\n", countOfPathsFromJoltageRating);
  }
}