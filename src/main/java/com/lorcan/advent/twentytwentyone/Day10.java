package com.lorcan.advent.twentytwentyone;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day10 {

  private static final String INPUT = "twentytwentyone/day-10-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-10-sample-input.txt";

  private static final Set<Character> OPENERS = Set.of(
      '(', '[', '{', '<'
  );

  private static final Map<Character, Character> PAIRS = Map.of(
      ')', '(',
      ']', '[',
      '}', '{',
      '>', '<'
  );

  private static final Map<Character, Long> PART_ONE_SCORES = Map.of(
      ')', 3L,
      ']', 57L,
      '}', 1197L,
      '>', 25137L
  );

  private static final Map<Character, Long> PART_TWO_SCORES = Map.of(
      '(', 1L,
      '[', 2L,
      '{', 3L,
      '<', 4L
  );


  public static void main(String[] args) {
    List<String> sampleLines = LogAndLoad.readFile(SAMPLE_INPUT);
    List<String> lines = LogAndLoad.readFile(INPUT);

    List<LineResult> sampleLineResults = analyzeLines(sampleLines);
    List<LineResult> lineResults = analyzeLines(lines);

    partOne(sampleLineResults);
    partOne(lineResults);

    partTwo(sampleLineResults);
    partTwo(lineResults);
  }

  private static void partOne(List<LineResult> lineResults) {
    long sum = 0L;
    for (LineResult lineResult : lineResults) {
      if (lineResult.getIllegalCharacter().isPresent()) {
        char illegalCharacter = lineResult.getIllegalCharacter().get();
        long score = PART_ONE_SCORES.get(illegalCharacter);
        sum += score;
        LogAndLoad.log("Found illegal character %s, expected: %s: adding %d to sum %d", illegalCharacter, lineResult.getStack().peek(), score, sum);
      }
    }
  }

  private static List<LineResult> analyzeLines(List<String> lines) {
    List<LineResult> lineResults = new ArrayList<>();
    for (String line : lines) {
      Stack<Character> stack = new Stack<>();
      Optional<Character> illegalCharacter = Optional.empty();
      for (char c : line.toCharArray()) {
        if (OPENERS.contains(c)) {
          stack.add(c);
        } else {
          char peekedCharacter = stack.peek();
          if (PAIRS.get(c) != peekedCharacter) {
            illegalCharacter = Optional.of(c);
            break;
          }

          stack.pop();
        }
      }

      lineResults.add(new LineResult(line, stack, illegalCharacter));
    }
    return lineResults;
  }

  private static void partTwo(List<LineResult> lineResults) {
    LogAndLoad.log("Started with %d lines", lineResults.size());

    lineResults = lineResults.stream()
        .filter(lineResult -> lineResult.getIllegalCharacter().isEmpty())
        .collect(Collectors.toUnmodifiableList());

    LogAndLoad.log("Left with %d incomplete lines", lineResults.size());

    List<Long> scores = new ArrayList<>();
    for (LineResult lineResult : lineResults) {
      long score = autoCompleteScore(lineResult);
      scores.add(score);
      LogAndLoad.log("Score of %d for line: %s", score, lineResult.getLine());
    }

    long middleScore = scores.stream()
        .sorted(Comparator.naturalOrder())
        .collect(Collectors.toUnmodifiableList())
        .get((lineResults.size() -1) / 2);
    LogAndLoad.log("Middle score: %d", middleScore);
  }

  private static long autoCompleteScore(LineResult lineResult) {
    long score = 0;
    while (!lineResult.getStack().isEmpty()) {
      score *= 5;
      score += PART_TWO_SCORES.get(lineResult.getStack().pop());
    }
    return score;
  }

  static class LineResult {
    private final String line;
    private final Stack<Character> stack;
    private final Optional<Character> illegalCharacter;

    public LineResult(String line, Stack<Character> stack, Optional<Character> illegalCharacter) {
      this.line = line;
      this.stack = stack;
      this.illegalCharacter = illegalCharacter;
    }

    public Optional<Character> getIllegalCharacter() {
      return illegalCharacter;
    }

    public String getLine() {
      return line;
    }

    public Stack<Character> getStack() {
      return stack;
    }
  }
}
