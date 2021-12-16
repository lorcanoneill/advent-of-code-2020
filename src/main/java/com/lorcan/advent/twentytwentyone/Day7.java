package com.lorcan.advent.twentytwentyone;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day7 {

  private static final String INPUT = "twentytwentyone/day-7-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-7-sample-input.txt";

  public static void main(String[] args) {
    List<Integer> samplePositions = Arrays.stream(LogAndLoad.readFileAsString(SAMPLE_INPUT).split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    List<Integer> positions = Arrays.stream(LogAndLoad.readFileAsString(INPUT).split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    processWithTransform(samplePositions, Day7::singleStepTransform);
    processWithTransform(positions, Day7::singleStepTransform);

    processWithTransform(samplePositions, Day7::increasingStepTransform);
    processWithTransform(positions, Day7::increasingStepTransform);
  }

  private static int singleStepTransform(int i) {
    return i;
  }

  private static int increasingStepTransform(int i) {
    int result = 0;
    int j = 0;
    while (j <= i) {
      result += j;
      j++;
    }

    return result;
  }

  private static void processWithTransform(List<Integer> positions, Function<Integer, Integer> stepTransform) {
   int leastMovesRequired = Integer.MAX_VALUE;
   int index = -1;
   int maxPosition = Collections.max(positions);
   for (int i = 0; i <= maxPosition; i++) {
     int count = 0;
     for (Integer position : positions) {
       count += stepTransform.apply(
           Math.abs(position - i)
       );
     }
     if (count < leastMovesRequired) {
       leastMovesRequired = count;
       index = i;
     }
   }

   LogAndLoad.log("Least moves required: %d, index: %d", leastMovesRequired, index);
  }
}
