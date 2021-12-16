package com.lorcan.advent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day14 {
  private static final Pattern MASK = Pattern.compile("^mask\\s=\\s(.*)$");
  private static final Pattern MEM = Pattern.compile("^mem\\[(\\d*)\\]\\s=\\s(.*)$");

  public static void main(String[] args) {
    List<String> lines = Utils.readFile("day-14-input.txt");
    partOne(lines);
    partTwo(lines);
  }

  private static void partOne(List<String> lines) {
    String maskAsString = "";
    Map<Long, int[]> memory = new HashMap<>();
    for (String line : lines) {
      Matcher matcher = MASK.matcher(line);
      if (matcher.find()) {
        maskAsString = matcher.group(1);
        System.out.format("Changing mask: %s\n", maskAsString);
        continue;
      }
      matcher = MEM.matcher(line);
      matcher.find();
      long cellToWrite = Long.parseLong(matcher.group(1), 10);
      long valueToWrite = Long.parseLong(matcher.group(2), 10);
      System.out.format("Cell to write: %d, value to write: %d, mask: %s\n", cellToWrite, valueToWrite, maskAsString);

      int[] bitMask = to36BitMask(maskAsString);
      int[] valueToWriteAs36BitNumber = to36BitNumber(valueToWrite);
      int [] maskedValue = new int[36];
      for (int i = 0; i < 36; i++) {
        int maskedValueAtI = bitMask[i];
        if (maskedValueAtI != -1) {
          maskedValue[i] = maskedValueAtI;
        } else {
          maskedValue[i] = valueToWriteAs36BitNumber[i];
        }
      }

      memory.put(cellToWrite, maskedValue);
    }

    long sum = 0;
    for (Map.Entry<Long, int[]> entry : memory.entrySet()) {
      long num = from36BitNumber(entry.getValue());
      System.out.format("Memory location %d contains %d\n", entry.getKey(), num);
      if (num != 0) {
        sum += num;
      }
    }

    System.out.format("Sum: %d\n", sum);
  }

  private static void partTwo(List<String> lines) {
    String maskAsString = "";
    Map<Long, int[]> memory = new HashMap<>();
    for (String line : lines) {
      Matcher matcher = MASK.matcher(line);
      if (matcher.find()) {
        maskAsString = matcher.group(1);
        System.out.format("Changing mask: %s\n", maskAsString);
        continue;
      }
      matcher = MEM.matcher(line);
      matcher.find();
      long cellToWrite = Long.parseLong(matcher.group(1), 10);
      long valueToWrite = Long.parseLong(matcher.group(2), 10);
      System.out.format("Cell to write: %d, value to write: %d, mask: %s\n", cellToWrite, valueToWrite, maskAsString);

      int[] bitMask = to36BitMask(maskAsString);
      int[] valueToWriteAs36BitNumber = to36BitNumber(valueToWrite);
      int[] memoryToWriteAs36BitNumber = to36BitNumber(cellToWrite);
      List<Integer> floatingBits = new ArrayList<>();
      for (int i = 0; i < 36; i++) {
        int maskedValueAtI = bitMask[i];
        if (maskedValueAtI == 1) {
          memoryToWriteAs36BitNumber[i] = 1;
        } else if (maskedValueAtI == -1) {
          memoryToWriteAs36BitNumber[i] = -1;
          floatingBits.add(i);
        }
      }

      if (floatingBits.isEmpty()) {
        memory.put(cellToWrite, valueToWriteAs36BitNumber);
      } else {
        int numberOfMemoryAddresses = (int) Math.pow(2, floatingBits.size());
        System.out.format("Writing %d memory addresses for cell %d - floating bits: %s\n", numberOfMemoryAddresses, cellToWrite, floatingBits.stream().map(String::valueOf).collect(Collectors.joining(",")));
        for (int i = 0; i < numberOfMemoryAddresses; i++) {
          int[] iAs36BitNumber = to36BitNumber(i);
          int[] copiedMemoryAddress = copy(memoryToWriteAs36BitNumber);
          for (int j = 0; j < floatingBits.size(); j++) {
            int offset = floatingBits.get(j);
            copiedMemoryAddress[offset] = iAs36BitNumber[j];
          }
          long copiedMemoryAddressAsLong = from36BitNumber(copiedMemoryAddress);
          System.out.format("Writing %d as a memory address for cell %d\n", copiedMemoryAddressAsLong, cellToWrite);
          memory.put(copiedMemoryAddressAsLong, valueToWriteAs36BitNumber);
        }
      }
    }

    long sum = 0;
    for (Map.Entry<Long, int[]> entry : memory.entrySet()) {
      long num = from36BitNumber(entry.getValue());
      System.out.format("Memory location %d contains %d\n", entry.getKey(), num);
      if (num != 0) {
        sum += num;
      }
    }

    System.out.format("Sum: %d\n", sum);
  }

  private static int[] copy(int[] arr) {
    int[] arr2 = new int[arr.length];
    for (int i = 0; i < arr.length; i++) {
      arr2[i] = arr[i];
    }
    return arr2;
  }

  private static long from36BitNumber(int[] arr) {
    long num = 0;
    for (int i = 0; i < 36; i++) {
      if (arr[i] == 1) {
        num += Math.pow(2, i);
      }
    }

    return num;
  }

  private static int[] to36BitMask(String mask) {
    int[] arr = new int[36];
    for (int i = 0; i < mask.length(); i++) {
      char c = mask.charAt(mask.length() - 1 - i);
      if (c == 'X') {
        arr[i] = -1;
      } else {
        arr[i] = Integer.parseInt(String.valueOf(c));
      }
    }
    return arr;
  }

  private static int[] to36BitNumber(long number) {
    int[] arr = new int[36];
    for (int i = 35; i >= 0; i--) {
      double pow = Math.pow(2, i);
      if (number - pow < 0) {
        arr[i] = 0;
        continue;
      }

      arr[i] = 1;
      number = (long) (number - pow);
    }

    return arr;
  }
}
