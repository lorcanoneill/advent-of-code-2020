package com.lorcan.advent.twentytwenty;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.lorcan.advent.utils.LogAndLoad;
import com.lorcan.advent.twentytwenty.Day7.BagRule.BagInformation;

public class Day7 {
  private static final String SHINY_GOLD = "shiny gold";
  private static final Pattern BAG_INFORMATION_PATTERN = Pattern.compile("^(\\d+)\\s+(.*)\\.?$");

  public static void main(String[] args) {
    List<BagRule> bagRules = LogAndLoad.readFile("twentytwenty/day-7-input.txt")
        .stream()
        .map(BagRule::fromString)
        .collect(Collectors.toUnmodifiableList());
    partOne(bagRules);
    partTwo(bagRules);
  }

  private static void partOne(List<BagRule> bagRules) {
    Set<String> containShinyGoldDirectly = new HashSet<>();
    Multimap<String, String> bagRulesMultimap = HashMultimap.create();
    bagRules.forEach(bagRule -> {
      Set<String> bagColours = bagRule.getBagInformations().stream().map(BagInformation::getBagColour)
          .collect(Collectors.toUnmodifiableSet());
      bagRulesMultimap.putAll(bagRule.getBagColour(), bagColours);
      if (bagColours.contains(SHINY_GOLD)) {
        containShinyGoldDirectly.add(bagRule.getBagColour());
      }
    });


    System.out.format("Initially can contain shiny gold: %d\n", containShinyGoldDirectly.size());
    HashMultimap<String, String> invertedBagRules = Multimaps.invertFrom(bagRulesMultimap, HashMultimap.create());
    Set<String> allCanContainShinyGold = new HashSet<>();
    allCanContainShinyGold.addAll(containShinyGoldDirectly);
    for (String bagColour : containShinyGoldDirectly) {
      allCanContainShinyGold.addAll(findMatches(invertedBagRules, bagColour));
    }

    System.out.format("Can eventually contain shiny gold: %d\n", allCanContainShinyGold.size());
  }

  private static void partTwo(List<BagRule> bagRules) {
    Optional<BagRule> maybeBagRuleForShinyGold = bagRules.stream().filter(bagRule -> bagRule.getBagColour().equals(SHINY_GOLD)).findAny();

    int bagsCount = 0;
    if (maybeBagRuleForShinyGold.isPresent()) {
      BagRule bagRule = maybeBagRuleForShinyGold.get();
      bagsCount += findBagCounts(1, bagsCount, bagRules, bagRule.getBagColour());
    }

    System.out.format("Bags count for shiny gold: %d\n", bagsCount);
  }

  private static Integer findBagCounts(int multiplier, int currentCount, List<BagRule> bagRules, String bagColour) {
    int count = 0;
    Optional<BagRule> maybeBagRule = bagRules.stream().filter(bagRule -> bagRule.getBagColour().equals(bagColour)).findFirst();
    if (maybeBagRule.isPresent()) {
      for (BagInformation bagInformation : maybeBagRule.get().getBagInformations()) {
        count += multiplier * bagInformation.getNumberOfBags();
        System.out.format("Found %d bags for %s, total count this batch: %d, current count: %d\n", bagInformation.getNumberOfBags(), bagInformation.getBagColour(), count, currentCount + count);
        int bagsInThatBag = findBagCounts(multiplier * bagInformation.getNumberOfBags(), currentCount + count, bagRules, bagInformation.getBagColour());
        count += bagsInThatBag;
        System.out.format("Found %d bags in %s, total count this batch: %d, current count: %d\n", bagsInThatBag, bagInformation.getBagColour(), count, currentCount + count);
      }
    }

    return count;
  }

  private static Set<String> findMatches(Multimap<String, String> rules, String bagColour) {
    Set<String> matches = new HashSet<>();
    if (rules.containsKey(bagColour)) {
      rules.get(bagColour).forEach(bc -> {
        matches.add(bc);
        findMatches(rules, bc).forEach(matches::add);
      });
    }

    return matches;
  }

  static class BagRule {
    private final String bagColour;
    private final List<BagInformation> bagInformations;

    private BagRule(String bagColour, List<BagInformation> bagInformations) {
      this.bagColour = bagColour;
      this.bagInformations = bagInformations;
    }

    public String getBagColour() {
      return bagColour;
    }

    public List<BagInformation> getBagInformations() {
      return bagInformations;
    }

    public static BagRule fromString(String str) {
      String[] arr = str.split(" bags contain ");

      String bagColour = arr[0];
      if (arr[1].equals("no other bags.")) {
        return new BagRule(bagColour, Collections.emptyList());
      }

      return new BagRule(
          bagColour,
          Arrays.stream(arr[1].split(", ")).map(BagInformation::fromString).collect(Collectors.toUnmodifiableList())
      );
    }

    static class BagInformation {
      private final int numberOfBags;
      private final String bagColour;

      private BagInformation(int numberOfBags, String bagColour) {
        this.numberOfBags = numberOfBags;
        this.bagColour = bagColour;
      }

      public int getNumberOfBags() {
        return numberOfBags;
      }

      public String getBagColour() {
        return bagColour;
      }

      public static BagInformation fromString(String str) {
        str = str.trim();
        Matcher matcher = BAG_INFORMATION_PATTERN.matcher(str);
        if (!matcher.find()) {
          throw new RuntimeException("Could not build BagInformation from input string: " + str);
        }

        return new BagInformation(
            Integer.parseInt(matcher.group(1), 10),
            matcher.group(2).replaceAll("bags?", "").replaceAll("\\.", "").trim()
        );
      }
    }
  }
}
