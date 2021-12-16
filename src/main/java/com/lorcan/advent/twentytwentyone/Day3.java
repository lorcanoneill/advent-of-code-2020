package com.lorcan.advent.twentytwentyone;

import java.util.List;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day3 {

  private static final String INPUT = "twentytwentyone/day-3-input.txt";
  public static void main(String[] args) {
    List<String> binaryStrings = LogAndLoad.readFile(INPUT);
    int[][] bitCommonality = calculateBitCommonality(binaryStrings);

    partOne(bitCommonality);
    partTwo(binaryStrings, bitCommonality);
  }

  private static void partOne(int[][] bitCommonality) {
    int gammaRate = 0;
    int epsillonRate = 0;
    for (int i = bitCommonality.length - 1; i >= 0; i--) {
      int zeroes = bitCommonality[i][0];
      int ones = bitCommonality[i][1];
      double bitValue = Math.pow(2, bitCommonality.length - i - 1);
      if (ones > zeroes) {
        gammaRate += bitValue;
      } else {
        epsillonRate += bitValue;
      }
    }

    LogAndLoad.log("Gamma rate: %d, Epsillon rate: %d, product: %d", gammaRate, epsillonRate, gammaRate * epsillonRate);
  }

  private static int[][] calculateBitCommonality(List<String> binaryStrings) {
    int lengthOfBinaryString = binaryStrings.get(0).length();
    int[][] bitCommonality = new int[lengthOfBinaryString][2];
    for (int i = 0; i < lengthOfBinaryString; i++) {
      bitCommonality[i][0] = 0;
      bitCommonality[i][1] = 0;
    }

    for (int i = 0; i < binaryStrings.size(); i++) {
      String binaryString = binaryStrings.get(i);
      char[] charArray = binaryString.toCharArray();
      for (int j = 0; j < charArray.length; j++) {
        int bit = Integer.parseInt(String.valueOf(charArray[j]));
        bitCommonality[j][bit] = bitCommonality[j][bit] + 1;
      }
    }

    return bitCommonality;
  }

  private static int fromBinaryString(String binaryString) {
    int result = 0;
    char[] chars = binaryString.toCharArray();
    for (int i = binaryString.length() - 1; i >= 0; i--) {
      int bit = Integer.parseInt(String.valueOf(chars[i]));
      if (bit == 1) {
        result += Math.pow(2, binaryString.length() - i - 1);
      }
    }
    return result;
  }

  private static void partTwo(List<String> binaryStrings, int[][] bitCommonality) {
    String oxygenGeneratorRatingStr = filterForOxygenGeneratorRating(binaryStrings, bitCommonality);

    String co2ScrubberRatingStr = filterForCo2ScrubberRating(binaryStrings, bitCommonality);

    int oxygenGeneratorRating = fromBinaryString(oxygenGeneratorRatingStr);
    int co2ScrubberRating = fromBinaryString(co2ScrubberRatingStr);
    LogAndLoad.log("Oxygen Generator Rating: %d, CO2 Scrubber Rating: %d, product: %d", oxygenGeneratorRating, co2ScrubberRating, oxygenGeneratorRating * co2ScrubberRating);
  }

  private static String filterForOxygenGeneratorRating(List<String> binaryStrings, int[][] bitCommonality) {
    int bit = 0;
    while (true) {
      final int finalBit = bit;
      String moreCommonBit = String.valueOf(getMoreCommonBit(bitCommonality, bit));

      binaryStrings = binaryStrings.stream()
          .filter(b -> b.substring(finalBit, finalBit + 1).equals(moreCommonBit))
          .collect(Collectors.toUnmodifiableList());
      if (binaryStrings.size() == 1) {
        return binaryStrings.get(0);
      }
      bit++;
      bitCommonality = calculateBitCommonality(binaryStrings);
    }
  }

  private static String filterForCo2ScrubberRating(List<String> binaryStrings, int[][] bitCommonality) {
    int bit = 0;
    while (true) {
      final int finalBit = bit;
      String lessCommonBit = String.valueOf(getLessCommonBit(bitCommonality, bit));

      binaryStrings = binaryStrings.stream()
          .filter(b -> b.substring(finalBit, finalBit + 1).equals(lessCommonBit))
          .collect(Collectors.toUnmodifiableList());
      if (binaryStrings.size() == 1) {
        return binaryStrings.get(0);
      }
      bit++;
      bitCommonality = calculateBitCommonality(binaryStrings);
    }
  }

  private static int getMoreCommonBit(int[][] bitCommonality, int n) {
    int zeroes = bitCommonality[n][0];
    int ones = bitCommonality[n][1];
    return (zeroes > ones) ? 0 : 1;
  }

  private static int getLessCommonBit(int[][] bitCommonality, int n) {
    int zeroes = bitCommonality[n][0];
    int ones = bitCommonality[n][1];
    return (zeroes <= ones) ? 0 : 1;
  }
}
