package com.lorcan.advent.twentytwentyone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day8 {

  private static final String SAMPLE_INPUT = "twentytwentyone/day-8-sample-input.txt";
  private static final String INPUT = "twentytwentyone/day-8-input.txt";

  private static final Set<Integer> LENGTHS_OF_UNIQUE_SEGMENTS = Set.of(
      2, 3, 4, 7
  );

  public static void main(String[] args) {
    List<String> sampleStrings = LogAndLoad.readFile(SAMPLE_INPUT);
    List<String> strings = LogAndLoad.readFile(INPUT);
    partOne(sampleStrings);
    partOne(strings);

    partTwo(sampleStrings);
    partTwo(strings);
  }

  private static void partOne(List<String> strings) {
    int count = 0;
    for (String string : strings) {
      count += Arrays.stream(string.split("\\|")[1].trim().split("\\s+"))
          .map(String::trim)
          .filter(s -> LENGTHS_OF_UNIQUE_SEGMENTS.contains(s.length()))
          .count();
    }
    LogAndLoad.log("Found %d instances of 1, 4, 7 and 8", count);
  }

  private static void partTwo(List<String> strings) {
    long sum = 0;
    for (String string : strings) {
      String[] arr = string.split("\\|");

      List<String> stringifiedNumbers = Arrays.stream(arr[0].trim().split("\\s+")).map(String::trim).collect(Collectors.toUnmodifiableList());

      Map<String, Integer> mappingOfStringToNumber = buildMapOfStringsToNumbers(stringifiedNumbers);

      List<String> outputNumbers = Arrays.stream(arr[1].split("\\s+")).map(String::trim)
          .filter(s -> !s.isBlank())
          .collect(Collectors.toUnmodifiableList());

      int outputNumber = 0;
      StringBuilder sb = new StringBuilder();
      for (int i = 0; i < outputNumbers.size(); i++) {
//      for (int i = outputNumbers.size() - 1; i >= 0; i--) {
        String scrambledString = outputNumbers.get(i);
        String key = mappingOfStringToNumber.keySet()
            .stream()
            .filter(s -> aContainsAllCharsOfB(s, scrambledString) && s.length() == scrambledString.length())
            .findFirst().get();

        sb.append(scrambledString + " ");

        outputNumber += mappingOfStringToNumber.get(key) * Math.pow(10, outputNumbers.size() - i - 1);
      }

      sb.append(": " + outputNumber);

      LogAndLoad.log(sb.toString());

      sum += outputNumber;
    }

    LogAndLoad.log("Sum: %d", sum);
  }

  private static Map<String, Integer> buildMapOfStringsToNumbers(List<String> stringifiedNumbers) {
    Map<String, Integer> mappingOfStringToNumber = new HashMap<>();
    // find 1
    String oneAsString = stringifiedNumbers.stream().filter(s -> s.length() == 2).findFirst().get();
    mappingOfStringToNumber.put(oneAsString, 1);

    // find 4
    String fourAsString = stringifiedNumbers.stream().filter(s -> s.length() == 4).findFirst().get();
    mappingOfStringToNumber.put(fourAsString, 4);

    // find 7
    String sevenAsString = stringifiedNumbers.stream().filter(s -> s.length() == 3).findFirst().get();
    mappingOfStringToNumber.put(sevenAsString, 7);

    // find 8
    String eightAsString = stringifiedNumbers.stream().filter(s -> s.length() == 7).findFirst().get();
    mappingOfStringToNumber.put(eightAsString, 8);

    // find 6
    String sixAsString = stringifiedNumbers
        .stream()
        .filter(s -> !aContainsAllCharsOfB(s, oneAsString) && s.length() == 6)
        .findFirst().get();
    mappingOfStringToNumber.put(sixAsString, 6);

    // use this to distinguish 5 and 2
    char topRightCharacter = getCharsOfBANotInA(sixAsString, sevenAsString).get(0);

    // find 2
    String twoAsString = stringifiedNumbers
        .stream()
        .filter(s -> s.contains(String.valueOf(topRightCharacter)) && !aContainsAllCharsOfB(s, oneAsString) && s.length() == 5)
        .findFirst().get();
    mappingOfStringToNumber.put(twoAsString, 2);

    // find 5
    String fiveAsString = stringifiedNumbers
        .stream()
        .filter(s -> !s.contains(String.valueOf(topRightCharacter)) && s.length() == 5)
        .findFirst().get();
    mappingOfStringToNumber.put(fiveAsString, 5);

    // find 3
    String threeAsString = stringifiedNumbers
        .stream()
        .filter(s -> aContainsAllCharsOfB(s, oneAsString) && s.length() == 5)
        .findFirst().get();
    mappingOfStringToNumber.put(threeAsString, 3);


    // find the middle character
    char middleCharacter = findCharactersInCommon(List.of(twoAsString, threeAsString, fourAsString, fiveAsString, sixAsString, eightAsString))
        .get(0);

    // find 0
    String zeroAsString = stringifiedNumbers
        .stream()
        .filter(s -> !s.contains(String.valueOf(middleCharacter)) && s.length() == 6)
        .findFirst().get();
    mappingOfStringToNumber.put(zeroAsString, 0);

    // find 9
    String nineAsString = stringifiedNumbers
        .stream()
        .filter(s -> !mappingOfStringToNumber.keySet().contains(s))
        .findFirst().get();
    mappingOfStringToNumber.put(nineAsString, 9);
    return mappingOfStringToNumber;
  }

  private static boolean aContainsAllCharsOfB(String a, String b) {
    return getCharsOfBANotInA(a, b).isEmpty();
  }

  private static List<Character> getCharsOfBANotInA(String a, String b) {
    List<Character> characters = new ArrayList<>();
    for (char c : b.toCharArray()) {
      if (!a.contains(String.valueOf(c))) {
        characters.add(c);
      }
    }

    return characters;
  }

  private static List<Character> findCharactersInCommon(List<String> strings) {
    Set<Character> characters = new HashSet<>();
    for (String str : strings) {
      for (char c : str.toCharArray()) {
        characters.add(c);
      }
    }

    return characters.stream()
        .filter(c -> strings.stream().allMatch(s -> s.contains(String.valueOf(c))))
        .collect(Collectors.toUnmodifiableList());
  }
}
