package com.lorcan.advent.twentytwentyone;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day6 {

  private static final String INPUT = "twentytwentyone/day-6-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-6-sample-input.txt";

  public static void main(String[] args) {
    List<Integer> sampleIntegers = Arrays.stream(LogAndLoad.readFileAsString(SAMPLE_INPUT).split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    List<Integer> integers = Arrays.stream(LogAndLoad.readFileAsString(INPUT).split(","))
        .map(Integer::parseInt)
        .collect(Collectors.toList());

    process(sampleIntegers, 80);
    process(integers, 80);

    process(sampleIntegers, 256);
    process(integers, 256);
  }

  private static void process(List<Integer> integers, int daysToSimulate) {
   int days = 1;
   long countOfFish = 0;

   Map<Integer, Long> counts = new HashMap<>();
   for (int number : integers) {
     counts.put(number, counts.getOrDefault(number, 0L) + 1);
   }

   while (days <= daysToSimulate) {
     Map<Integer, Long> newMap = new HashMap<>();
     long sixes = 0L;
     for (int key : counts.keySet()) {
       long countForKey = counts.get(key);
       if (key == 0) {
         newMap.put(8, countForKey);
         sixes += countForKey;
       } else if (key == 7) {
         sixes += countForKey;
       } else {
         newMap.put(key - 1, countForKey);
       }

       if (sixes > 0L) {
         newMap.put(6, sixes);
       }
     }

     countOfFish = 0;
     for (long value : newMap.values()) {
       countOfFish += value;
     }
     days++;
     counts = newMap;
   }
    LogAndLoad.log("Fish after %d days: %d", days, countOfFish);
  }
}
