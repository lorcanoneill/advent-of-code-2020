package com.lorcan.advent.twentytwentyone;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.lorcan.advent.utils.LogAndLoad;

public class Day14 {

  private static final String INPUT = "twentytwentyone/day-14-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-14-sample-input.txt";

  public static void main(String[] args) {
    List<String> lines = LogAndLoad.readFile(INPUT);

    String template = lines.get(0);

    Map<String, String> rules = new HashMap<>();
    Map<String, String> subStringToCharacterMappings = new HashMap<>();
    Multimap<String, String> rulesMultimap = HashMultimap.create();

    for (int i = 2; i < lines.size(); i++) {
      String[] arr = lines.get(i).split(" -> ");
      String[] keyArr = arr[0].split("");

      rules.put(arr[0], arr[1] + keyArr[1]);
      rulesMultimap.put(arr[0], keyArr[0] + arr[1]);
      rulesMultimap.put(arr[0], arr[1] + keyArr[1]);
      subStringToCharacterMappings.put(arr[0], arr[1]);
    }

    naive(template, rules, 10);
    nonNaive(template, rulesMultimap, subStringToCharacterMappings, 10);
    nonNaive(template, rulesMultimap, subStringToCharacterMappings, 40);
  }

  private static void naive(String template, Map<String, String> rules, int steps) {
    int step = 0;
    while (step < steps) {
      String newTemplate = "";
      for (int i = template.length(); i >= 2; i--) {
        String subString = template.substring(i - 2, i);
        if (rules.containsKey(subString)) {
          newTemplate = rules.get(subString) + newTemplate;
          if (i == 2) {
            newTemplate = template.substring(0, 1) + newTemplate;
          }
        } else {
          newTemplate = subString.concat(newTemplate);
        }
      }

      template = newTemplate;
      step++;
      LogAndLoad.log("After step %d, template is %d characters long", step, template.length());
    }

    Map<Character, Long> counts = new HashMap<>();
    for (char c : template.toCharArray()) {
      counts.put(c, counts.getOrDefault(c, 0L) + 1);
    }

    long mostCommonElementCount = counts.values().stream().max(Comparator.naturalOrder()).get();
    long leastCommonElementCount = counts.values().stream().min(Comparator.naturalOrder()).get();
    LogAndLoad.log("Most common: %d, least common: %d, most common - least common: %d", mostCommonElementCount, leastCommonElementCount, mostCommonElementCount - leastCommonElementCount);
  }

  private static void nonNaive(String template, Multimap<String, String> rules, Map<String, String> subStringToCharacterMappings, int steps) {
    Map<String, Long> substringCounts = new HashMap<>();
    Map<Character, Long> characterCounts = new HashMap<>();
    for (int i = 0; i < template.length() - 1; i++) {
      String substring = template.substring(i, i + 2);
      substringCounts.put(substring, substringCounts.getOrDefault(substring, 0L) + 1);
    }

    for (char c  : template.toCharArray()) {
      characterCounts.put(c, characterCounts.getOrDefault(c, 0L) + 1);
    }

    int step = 0;
    while (step < steps) {
      Map<String, Long> newCounts = new HashMap<>();
      for (String key : substringCounts.keySet()) {
        long count = substringCounts.get(key);
        if (subStringToCharacterMappings.containsKey(key)) {
          char character = subStringToCharacterMappings.get(key).toCharArray()[0];
          characterCounts.put(character, characterCounts.getOrDefault(character, 0L) + count);
        }

        Collection<String> rulesForKey = rules.get(key);
        if (!rulesForKey.isEmpty()) {
          for (String newKey : rulesForKey) {
            newCounts.put(newKey, newCounts.getOrDefault(newKey, 0L) + count);
          }
        } else {
          newCounts.put(key, count);
        }


      }

      substringCounts = newCounts;
      step++;
    }

    long mostCommonElementCount = characterCounts.values().stream().max(Comparator.naturalOrder()).get();
    long leastCommonElementCount = characterCounts.values().stream().min(Comparator.naturalOrder()).get();
    LogAndLoad.log("Most common: %d, least common: %d, most common - least common: %d", mostCommonElementCount, leastCommonElementCount, mostCommonElementCount - leastCommonElementCount);
  }
}
