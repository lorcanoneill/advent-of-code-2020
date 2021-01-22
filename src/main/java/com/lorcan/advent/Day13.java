package com.lorcan.advent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class Day13 {
  public static void main(String[] args) {
    List<String> lines = Utils.readFile("day-13-input.txt");

    long timestamp = Long.parseLong(lines.get(0), 10);
    Set<Long> ids = Arrays.stream(lines.get(1).split(","))
        .filter(str -> !str.equals("x"))
        .map(str -> Long.parseLong(str, 10))
        .collect(Collectors.toUnmodifiableSet());
    partOne(timestamp, ids);
    partTwo(Arrays.stream(lines.get(1).split(",")).collect(Collectors.toUnmodifiableList()));
  }

  private static void partOne(long timestamp, Set<Long> ids) {
    long inputTimestamp = timestamp;
    while (true) {
      long t = timestamp;
      Optional<Long> maybeId = ids.stream()
          .filter(id -> t % id == 0)
          .findAny();

      if (maybeId.isPresent()) {
        System.out.format("Can take bus %d at timestamp %d (original timestamp: %d). Diff: %d minutes. Product: %d\n",
          maybeId.get(), timestamp, inputTimestamp, (timestamp - inputTimestamp), ((timestamp - inputTimestamp) * maybeId.get())
        );
        break;
      }
      timestamp++;
    }
  }

  private static void partTwo(List<String> busIds) {
    List<Integer> offsets = new ArrayList<>();
    long firstBusId = Long.parseLong(busIds.get(0), 10);
    for (int i = 1; i < busIds.size(); i++) {
      if (!busIds.get(i).equals("x")) {
        offsets.add(i);
      }
    }

    System.out.format("First bus id: %d\n", firstBusId);
    long sum = 1;
    for (int offset : offsets) {
      long busId = Long.parseLong(busIds.get(offset), 10);
      sum *= busId;
      System.out.format("Desired busId %d at offset %d\n", busId, offset);
    }

    System.out.format("Product of offset bus ids: %d\n", sum);
    System.out.format("Product of all bus ids: %d\n", firstBusId * sum);

    // numerator for busId 37 at offset 23 changes by 37 for each match
    // numerator for busId 467 at offset 29 changes by 467 for each match
    // numerator for busId 23 at offset 37 changes by ? for each match
    // numerator for busId 13 at offset 42 changes by ? for each match
    // numerator for busId 17 at offset 46 changes by ? for each match
    // numerator for busId 19 at offset 48 changes by ? for each match
    // numerator for busId 443 at offset 60 changes by 443 for each match
    // numerator for busId 41 at offset 101 changes by 41 for each match

    Set<Long> seen = new HashSet<>();

    List<Integer> moarOffsets = new ArrayList<>();
    moarOffsets.add(60);
    moarOffsets.add(101);
//    long numerator = 1L;
//    long numerator = 141095012148L;
    long oneHundredTrillion = 100_000_000_000_000L;
    long modulus = oneHundredTrillion % firstBusId;
    long p = oneHundredTrillion + modulus;
    long numerator = 1;
    Map<Long, Long> busIdNumerators = new HashMap<>();
    boolean solved = false;
    long incrementForNumerator = 1;
    while (!solved) {
      long thisNumerator = numerator;
      if (!busIdNumerators.isEmpty()) {
        if (!busIdNumerators.entrySet().stream().allMatch(entry -> {
          return (thisNumerator - entry.getValue()) % entry.getKey() == 0;
        })) {
          numerator += incrementForNumerator;
          continue;
        }
      }
      long product = firstBusId * numerator;
      boolean matched = true;
      for (int offset : offsets) {
        long desiredProduct = product + offset;
        long busId = Long.parseLong(busIds.get(offset), 10);
        boolean matches = (desiredProduct % busId == 0);
        if (!matches) {
          matched = false;
          break;
        } else {
          if (incrementForNumerator % busId != 0) {
            incrementForNumerator = incrementForNumerator * busId;
          }
          if (!seen.contains(busId)) {
            System.out.format("Offset %d, busId: %d matched! Numerator: %d, increment: %d, product: %d, desired product: %d, potential answer: %d\n",
                offset, busId, numerator, incrementForNumerator, product, desiredProduct, numerator * firstBusId
            );
            seen.add(busId);
            busIdNumerators.put(busId, numerator);
          }
        }
      }
      numerator += incrementForNumerator;
      solved = matched;
    }
    System.out.format("Timestamp: %d\n", numerator * firstBusId);

  }
}
