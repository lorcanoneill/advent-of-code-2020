package com.lorcan.advent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day2 {

  private static final Pattern PASSWORD_POLICY_PATTERN = Pattern.compile("^(\\d+)-(\\d+)\\s+([a-z]):\\s+(.*)$");

  public static void main(String[] args) {
    List<PasswordPolicy> passwordPolicies = Utils.readFile("day-2-input.txt")
        .stream()
        .map(PasswordPolicy::fromString)
        .collect(Collectors.toUnmodifiableList());
    partOne(passwordPolicies);
    partTwo(passwordPolicies);
  }

  private static void partOne(List<PasswordPolicy> passwordPolicies) {
    System.out.println("Part One!");
    int valid = 0;
    for (PasswordPolicy passwordPolicy : passwordPolicies) {
      if (passwordPolicy.matchesPolicy()) {
        valid++;
      } else {
        System.out.println(String.format("Password %s contains the wrong number of instances of character %c (must be between %d and %d, was %d)",
            passwordPolicy.getPassword(),
            passwordPolicy.getCharacter(),
            passwordPolicy.getLowerBound(),
            passwordPolicy.getUpperBound(),
            passwordPolicy.getInstancesOfCharacter()
            )
        );
      }
    }

    System.out.println(String.format("Found %d valid passwords", valid));
  }

  private static void partTwo(List<PasswordPolicy> passwordPolicies) {
    System.out.println("Part Two!");
    int valid = 0;
    for (PasswordPolicy passwordPolicy : passwordPolicies) {
      if (passwordPolicy.hasCharacterAtOneBoundOnly()) {
        valid++;
      } else {
        System.out.println(String.format("Password %s contains the wrong number of instances of character %c (must be at index %d *or* index %d, is %s index %d and %s index %d)",
            passwordPolicy.getPassword(),
            passwordPolicy.getCharacter(),
            passwordPolicy.getLowerBound(),
            passwordPolicy.getUpperBound(),
            passwordPolicy.hasCharacterAtLowerBound() ? "at" : "not at",
            passwordPolicy.getLowerBound(),
            passwordPolicy.hasCharacterAtUpperBound() ? "at" : "not at",
            passwordPolicy.getUpperBound()
            )
        );
      }
    }

    System.out.println(String.format("Found %d valid passwords", valid));
  }

  static class PasswordPolicy {
    private final int lowerBound;
    private final int upperBound;
    private final char character;
    private final String password;
    private final int instancesOfCharacter;

    private PasswordPolicy(int lowerBound, int upperBound, char character, String password) {
      this.lowerBound = lowerBound;
      this.upperBound = upperBound;
      this.character = character;
      this.password = password;

      Map<Character, Integer> countOfCharacters = new HashMap<>();
      for (char c : password.toCharArray()) {
        countOfCharacters.put(c, countOfCharacters.getOrDefault(c, 0) + 1);
      }
      this.instancesOfCharacter = countOfCharacters.getOrDefault(character, 0);
    }

    public boolean matchesPolicy() {
      return instancesOfCharacter >= lowerBound && instancesOfCharacter <= upperBound;
    }

    public boolean hasCharacterAtLowerBound() {
      return password.charAt(lowerBound - 1) == character;
    }

    public boolean hasCharacterAtUpperBound() {
      return password.charAt(upperBound - 1) == character;
    }

    public boolean hasCharacterAtOneBoundOnly() {
      return hasCharacterAtLowerBound() ^ hasCharacterAtUpperBound();
    }

    public char getCharacter() {
      return character;
    }

    public String getPassword() {
      return password;
    }

    public int getLowerBound() {
      return lowerBound;
    }

    public int getUpperBound() {
      return upperBound;
    }

    public int getInstancesOfCharacter() {
      return instancesOfCharacter;
    }

    public static PasswordPolicy fromString(String str) {
      Matcher matcher = PASSWORD_POLICY_PATTERN.matcher(str);
      if (!matcher.find()) {
        throw new RuntimeException("Could not match password policy for input string " + str);
      }

      int lowerBound = Integer.parseInt(matcher.group(1));
      int upperBound = Integer.parseInt(matcher.group(2));
      char character = matcher.group(3).charAt(0);
      String password = matcher.group(4);
      return new PasswordPolicy(lowerBound, upperBound, character, password);
    }
  }
}
