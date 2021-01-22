package com.lorcan.advent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

public class Day18 {

  private static final String ADD = "+";
  private static final String LEFT_PAREN = "(";
  private static final String RIGHT_PAREN = ")";
  private static final String MULTIPLY = "*";

  public static void main(String[] args) {
    List<Expression> expressions = Utils.readFile("day-18-sample-input.txt")
        .stream()
        .map(Expression::fromString)
        .collect(Collectors.toUnmodifiableList());
    partOne(expressions);
  }

  private static void partOne(List<Expression> expressions) {
    long sum = 0;
    for (Expression expression : expressions) {
      sum += solveExpression(expression);
    }

    System.out.format("Sum: %d\n", sum);
  }

  private static long solveExpression(Expression expression) {
    int parenCount = 0;
    long result = 0;
    List<Token> thisExpression = new ArrayList<>();
    for (Token token : expression.getTokens()) {
      if (token.getTokenType() == TokenType.LEFT_PAREN) {
        parenCount++;
      }
      else if (token.getTokenType() == TokenType.RIGHT_PAREN) {
        parenCount--;
        if (parenCount == 0) {
          result += solveExpression(new Expression(thisExpression));
        }
      }
      else {
        thisExpression.add(token);
      }
    }
  }

  static class Expression {
    private final List<Token> tokens;

    public Expression(List<Token> tokens) {
      this.tokens = tokens;
    }

    public List<Token> getTokens() {
      return tokens;
    }

    public static Expression fromString(String str) {
      String[] arr = str
          .replaceAll("\\(", "(  ")
          .replaceAll("\\)", " )")
          .split(" ");

      return new Expression(
          Arrays.stream(arr)
            .map(s -> {
              if (s.equals(ADD)) {
                return new Token(TokenType.ADD);
              } else if(s.equals(MULTIPLY)) {
                return new Token(TokenType.MULTIPLY);
              } else if (s.equals(LEFT_PAREN)) {
                return new Token(TokenType.LEFT_PAREN);
              } else if (s.equals(RIGHT_PAREN)) {
                return new Token(TokenType.RIGHT_PAREN);
              } else {
                return new Token(TokenType.NUMBER, Optional.of(Integer.parseInt(s)));
              }
            })
          .collect(Collectors.toUnmodifiableList())
        );
    }
  }

  static class Token {
    private final TokenType tokenType;
    private final Optional<Integer> number;

    Token(TokenType tokenType) {
      this(tokenType, Optional.empty());
    }

    Token(TokenType tokenType, Optional<Integer> number) {
      this.tokenType = tokenType;
      this.number = number;
    }

    public TokenType getTokenType() {
      return tokenType;
    }

    public Optional<Integer> getNumber() {
      return number;
    }
  }

  enum TokenType {
    LEFT_PAREN,
    RIGHT_PAREN,
    ADD,
    MULTIPLY,
    NUMBER;
  }
}
