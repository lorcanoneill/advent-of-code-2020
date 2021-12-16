package com.lorcan.advent.twentytwenty;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day4 {

  private static final Set<String> REQUIRED_FIELDS = Set.of(
      "byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"
  );

  private static final Set<String> VALID_EYE_COLOURS = Set.of(
      "amb", "blu", "brn", "gry", "grn", "hzl", "oth"
  );

  private static final Pattern HAIR_COLOUR = Pattern.compile("^#[0-9a-f]{6}$");
  private static final Pattern HEIGHT = Pattern.compile("^(\\d+)(cm|in)");
  private static final Pattern PASSPORT = Pattern.compile("^\\d{9}$");

  public static void main(String[] args) {
    List<Map<String, Object>> passports = Arrays.stream(LogAndLoad.readFileAsString("twentytwenty/day-4-input.txt")
        .split("\\n\\n"))
        .map(Day4::buildPassport)
        .collect(Collectors.toUnmodifiableList());
    System.out.format("Found %d passports\n", passports.size());
    partOne(passports);
    partTwo(passports);
  }

  private static void partOne(List<Map<String, Object>> passports) {
    long valid = passports.stream()
        .filter(Day4::isPassportValid)
        .count();
    System.out.format("Found %d valid passports for part one\n", valid);
  }

  private static void partTwo(List<Map<String, Object>> passports) {
    long valid = passports.stream()
        .filter(Day4::isPassportValidWithStricterValidation)
        .count();
    System.out.format("Found %d valid passports for part two\n", valid);
  }

  private static Map<String, Object> buildPassport(String str) {
    return Arrays.stream(str.replaceAll("\\n", " ").trim().split("\\s"))
        .map(s -> s.split(":"))
        .collect(Collectors.toUnmodifiableMap(
            arr -> arr[0],
            arr -> arr[1]
        ));
  }

  private static boolean isPassportValid(Map<String, Object> passport) {
    boolean isValid = passport.keySet().containsAll(REQUIRED_FIELDS);

    if (!isValid) {
      System.out.format("Passport is invalid: %s\n", passport);
    }

    return isValid;
  }

  private static boolean isPassportValidWithStricterValidation(Map<String, Object> passport) {
    int birthYear = toInt(passport.getOrDefault("byr", -1));
    int issueYear = toInt(passport.getOrDefault("iyr", -1));
    int expirationYear = toInt(passport.getOrDefault("eyr", -1));
    String height = String.valueOf(passport.getOrDefault("hgt", ""));
    String eyeColour = String.valueOf(passport.getOrDefault("ecl", ""));
    String hairColour = String.valueOf(passport.getOrDefault("hcl", ""));
    String passportId = String.valueOf(passport.getOrDefault("pid", ""));

    if (birthYear < 1920 || birthYear > 2002) {
      System.out.format("byr value %d invalid for passport %s\n", birthYear, passport);
      return false;
    }

    if (issueYear < 2010 || issueYear > 2020) {
      System.out.format("iyr value %d invalid for passport %s\n", issueYear, passport);
      return false;
    }

    if (expirationYear < 2020 || expirationYear > 2030) {
      System.out.format("eyr value %d invalid for passport %s\n", expirationYear, passport);
      return false;
    }

    Matcher heightMatcher = HEIGHT.matcher(height);
    if (!heightMatcher.find()) {
      System.out.format("hgt value %s invalid for passport %s\n", height, passport);
      return false;
    }

    int heightNumber = Integer.parseInt(heightMatcher.group(1), 10);
    String heightUnit = heightMatcher.group(2);
    if (heightUnit.equals("in") && (heightNumber < 59 || heightNumber > 76)) {
      System.out.format("hgt value %s invalid for passport %s\n", height, passport);
      return false;
    }

    if (heightUnit.equals("cm") && (heightNumber < 150 || heightNumber > 193)) {
      System.out.format("hgt value %s invalid for passport %s\n", height, passport);
      return false;
    }

    if (!HAIR_COLOUR.matcher(hairColour).find()) {
      System.out.format("hcl value %s invalid for passport %s\n", hairColour, passport);
      return false;
    }

    if (!VALID_EYE_COLOURS.contains(eyeColour)) {
      System.out.format("ecl value %s invalid for passport %s\n", eyeColour, passport);
      return false;
    }

    if (!PASSPORT.matcher(passportId).find()) {
      System.out.format("pid value %s invalid for passport %s\n", passportId, passport);
      return false;
    }

    return true;
  }

  private static int toInt(Object obj) {
    return Integer.parseInt(String.valueOf(obj), 10);
  }
}
