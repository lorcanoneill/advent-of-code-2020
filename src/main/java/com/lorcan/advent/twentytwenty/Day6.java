package com.lorcan.advent.twentytwenty;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day6 {

  public static void main(String[] args) {
    List<List<String>> groups = Arrays.stream(LogAndLoad.readFileAsString("twentytwenty/day-6-input.txt").split("\n\n"))
        .map(str -> Arrays.stream(str.split("\n")).collect(Collectors.toUnmodifiableList()))
        .collect(Collectors.toUnmodifiableList());
    partOne(groups);
    partTwo(groups);
  }

  private static void partOne(List<List<String>> groups) {
    int sumOfCounts = 0;
    for (List<String> group : groups) {
      Set<Character> characters = new HashSet<>();
      group.forEach(passenger -> {
        for (char c : passenger.toCharArray()) {
          characters.add(c);
        }
      });
      sumOfCounts += characters.size();
    }

    System.out.format("Found total sum of counts for part one: %d\n", sumOfCounts);
  }

  private static void partTwo(List<List<String>> groups) {
    int sumOfCounts = 0;
    for (List<String> group : groups) {
      int numberOfPassengers = group.size();
      Map<Character, Integer> characterCounts = new HashMap<>();
      for (String passenger : group) {
        for (char c : passenger.toCharArray()) {
          characterCounts.put(c, characterCounts.getOrDefault(c, 0) + 1);
        }
      }

      long count = characterCounts.entrySet().stream()
          .filter(e -> e.getValue() == numberOfPassengers)
          .map(e -> e.getKey())
          .count();


      sumOfCounts += count;
    }

    System.out.format("Found total sum of counts for part two: %d\n", sumOfCounts);
  }
}
