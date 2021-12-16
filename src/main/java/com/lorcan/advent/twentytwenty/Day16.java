package com.lorcan.advent.twentytwenty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day16 {

  public static void main(String[] args) {
    List<String> lines = LogAndLoad.readFile("twentytwenty/day-16-input.txt");

    Target target = Target.RULE;
    List<Rule> rules = new ArrayList<>();
    Ticket yourTicket = null;
    List<Ticket> nearbyTickets = new ArrayList<>();
    for (String line : lines) {
      if (line.isBlank()) {
        if (target == Target.RULE) {
          target = Target.YOUR_TICKET;
        } else {
          target = Target.NEARBY_TICKETS;
        }
        continue;
      } else if (line.contains("ticket")) {
        continue;
      }

      if (target == Target.RULE) {
        rules.add(Rule.fromString(line));
      } else if (target == Target.YOUR_TICKET) {
        yourTicket = Ticket.fromString(line);
      } else {
        nearbyTickets.add(Ticket.fromString(line));
      }
    }

    assert yourTicket != null;

    partOne(rules, nearbyTickets);
    partTwo(rules, yourTicket, nearbyTickets);
  }

  private static void partOne(List<Rule> rules, List<Ticket> nearbyTickets) {
    int sumOfInvalidFields = 0;
    for (Ticket ticket : nearbyTickets) {
      for (int value : ticket.getValues()) {
        if (rules.stream().noneMatch(rule -> rule.isInBounds(value))) {
          sumOfInvalidFields += value;
        }
      }
    }

    System.out.format("Sum of invalid fields: %d\n", sumOfInvalidFields);
  }

  private static void partTwo(List<Rule> rules, Ticket yourTicket, List<Ticket> nearbyTickets) {
    // remove invalid tickets
    List<Ticket> validTickets = nearbyTickets.stream()
        .filter(ticket -> ticket.getValues().stream().allMatch(value -> {
          return rules.stream().anyMatch(rule -> rule.isInBounds(value));
        }))
        .collect(Collectors.toUnmodifiableList());

    Map<String, Map<Integer, Integer>> ruleToFieldCounts = new HashMap<>();
    for (Ticket ticket : validTickets) {
      for (int i = 0; i < ticket.getValues().size(); i++) {
        int value = ticket.getValues().get(i);
        for (Rule rule : rules) {
          if (rule.isInBounds(value)) {
            Map<Integer, Integer> counts = ruleToFieldCounts.getOrDefault(rule.getName(), new HashMap<>());
            counts.put(i, counts.getOrDefault(i, 0) + 1);
            ruleToFieldCounts.put(rule.getName(), counts);
          }
        }
      }
    }

    Map<String, Integer> fieldMappings = new HashMap<>();
    Set<Integer> matchedFields = new HashSet<>();
    while (fieldMappings.size() < yourTicket.getValues().size()) {
      for (Map.Entry<String, Map<Integer, Integer>> entry : ruleToFieldCounts.entrySet()) {
        if (fieldMappings.containsKey(entry.getKey())) {
          continue;
        }
        List<Integer> perfectlyMatchingFields = entry.getValue().entrySet().stream()
            .filter(e -> e.getValue() == validTickets.size() && !matchedFields.contains(e.getKey()))
            .map(Entry::getKey)
            .collect(Collectors.toUnmodifiableList());

        if (perfectlyMatchingFields.size() == 1) {
          System.out.format("Matched rule %s to field %d\n", entry.getKey(), perfectlyMatchingFields.get(0));
          matchedFields.add(perfectlyMatchingFields.get(0));
          fieldMappings.put(entry.getKey(), perfectlyMatchingFields.get(0));
        }
      }
    }

    long product = 1;
    for (Map.Entry<String, Integer> entry : fieldMappings.entrySet()) {
      if (entry.getKey().startsWith("departure")) {
        product *= yourTicket.getValues().get(entry.getValue());
        System.out.format("Found value %d for field %d (%s) on your ticket. Product is now: %d\n", yourTicket.getValues().get(entry.getValue()), entry.getValue(), entry.getKey(), product);
      }
    }

    System.out.format("product: %d\n", product);
  }

  enum Target {
    RULE,
    YOUR_TICKET,
    NEARBY_TICKETS;
  }

  static class Ticket {
    private final List<Integer> values;

    Ticket(List<Integer> values) {
      this.values = values;
    }

    public List<Integer> getValues() {
      return values;
    }

    public static Ticket fromString(String str) {
      return new Ticket(
          Arrays.stream(str.split(",")).map(Integer::parseInt).collect(Collectors.toUnmodifiableList())
      );
    }
  }

  static class Rule {
    private final String name;
    private final int lowerBound1;
    private final int upperBound1;
    private final int lowerBound2;
    private final int upperBound2;


    Rule(String name, int lowerBound1, int upperBound1, int lowerBound2, int upperBound2) {
      this.name = name;
      this.lowerBound1 = lowerBound1;
      this.upperBound1 = upperBound1;
      this.lowerBound2 = lowerBound2;
      this.upperBound2 = upperBound2;
    }

    public String getName() {
      return name;
    }

    public boolean isInBounds(int value) {
      return (lowerBound1 <= value && upperBound1 >= value)
          || (lowerBound2 <= value && upperBound2 >= value);
    }

    public static Rule fromString(String str) {
      String[] arr = str.split(": ");
      String name = arr[0];

      String[] boundses = arr[1].split(" or ");
      String[] bounds1 = boundses[0].split("-");
      String[] bounds2 = boundses[1].split("-");

      return new Rule(
          name,
          Integer.parseInt(bounds1[0]),
          Integer.parseInt(bounds1[1]),
          Integer.parseInt(bounds2[0]),
          Integer.parseInt(bounds2[1])
      );
    }
  }
}
