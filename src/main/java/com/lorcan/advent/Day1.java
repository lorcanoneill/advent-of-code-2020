package com.lorcan.advent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class Day1 {

  private static final String INPUT = "day-1-input.txt";

  public static void main(String[] args) {
    partOne();
    partTwo();
  }

  private static void partOne() {
    List<Integer> integers = Utils.readFile(INPUT)
        .stream().map(str -> Integer.parseInt(str.trim()))
        .collect(Collectors.toUnmodifiableList());

    Set<Integer> alreadySeen = new HashSet<>();

    for (Integer integer : integers) {
      alreadySeen.add(integer);
      int otherNumber = 2020 - integer;
      if (alreadySeen.contains(otherNumber)) {
        System.out.println(String.format("Solution found for part one: number 1 %d, number 2 %s, product: %d",          integer, otherNumber, integer * otherNumber
        ));
      }
    }
  }

  private static void partTwo() {
    List<Integer> integers = Utils.readFile(INPUT)
        .stream().map(str -> Integer.parseInt(str.trim()))
        .collect(Collectors.toUnmodifiableList());

    Multimap<Integer, Pair> pairs = HashMultimap.create();

    for (int i = 0; i < integers.size() - 1; i++) {
      for (int y = 1; y < integers.size(); y++) {
        Integer num1 = integers.get(i);
        Integer num2 = integers.get(y);

        int sum = num1 + num2;
        if (sum > 2020) {
          continue;
        }

        pairs.put(sum, new Pair(num1, num2));
      }
    }

    for (Integer integer : integers) {
      if (pairs.containsKey(2020 - integer)) {
        pairs.get(2020 - integer).forEach(pair -> {
          System.out.println(
              String.format("Solution found for part two: integer: %d, pair: %s, product: %d", integer, pair, integer * pair.getNum1() * pair.getNum2())
          );
        });

      }
    }
  }

  static class Pair {
    private final int num1;
    private final int num2;

    public Pair(int num1, int num2) {
      this.num1 = num1;
      this.num2 = num2;
    }

    public int getNum1() {
      return num1;
    }

    public int getNum2() {
      return num2;
    }

    @Override
    public String toString() {
      return String.format("num1=%d, num2=%d", num1, num2);
    }
  }
}
