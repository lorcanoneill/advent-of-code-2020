package com.lorcan.advent.twentytwentyone;

import java.util.List;
import java.util.stream.Collectors;

import com.lorcan.advent.Utils;

public class Day1 {

  private static final String INPUT = "twentytwentyone/day-1-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-1-sample-input.txt";

  public static void main(String[] args) {
    List<Integer> integers = Utils.readFile(INPUT)
        .stream().map(str -> Integer.parseInt(str.trim()))
        .collect(Collectors.toUnmodifiableList());
    partOne(integers);
    partTwo(integers);
  }

  private static void partOne(List<Integer> integers) {
    int increasedMeasurements = 0;
    int lastMeasurement = integers.get(0);

    for (int value : integers.stream().skip(1).collect(Collectors.toUnmodifiableList())) {
      if (value > lastMeasurement) {
        increasedMeasurements++;
      }

      lastMeasurement = value;
    }

    System.out.printf("Part one: Greater measurements: %s\n", increasedMeasurements);
  }

  private static void partTwo(List<Integer> integers) {
    int increasedMeasurements = 0;

    int value1 = integers.get(0);
    int value2 = integers.get(1);
    int value3 = integers.get(2);
    int currentMeasurerment = value1 + value2 + value3;

    for (int i = 1; i < integers.size() - 2; i++) {

      value1 = integers.get(i);
      value2 = integers.get(i + 1);
      value3 = integers.get(i + 2);

      int value = value1 + value2 + value3;
      if (value > currentMeasurerment) {
        increasedMeasurements++;
      }

      currentMeasurerment = value;
    }

    System.out.printf("Part two: Greater measurements: %s\n", increasedMeasurements);
  }
}
