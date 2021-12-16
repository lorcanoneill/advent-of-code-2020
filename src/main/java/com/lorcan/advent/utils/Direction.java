package com.lorcan.advent.utils;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public enum Direction {
  UP(point -> new Point(point.getX(), point.getY() - 1)),
  DOWN(point -> new Point(point.getX(), point.getY() + 1)),
  LEFT(point -> new Point(point.getX() - 1, point.getY())),
  RIGHT(point -> new Point(point.getX() + 1, point.getY())),
  UP_LEFT(point -> new Point(point.getX() - 1, point.getY() - 1)),
  UP_RIGHT(point -> new Point(point.getX() + 1, point.getY() - 1)),
  DOWN_LEFT(point -> new Point(point.getX() - 1, point.getY() + 1)),
  DOWN_RIGHT(point -> new Point(point.getX() + 1, point.getY() + 1));

  private final Function<Point, Point> transform;

  Direction(Function<Point, Point> transform) {
    this.transform = transform;
  }

  public Function<Point, Point> getTransform() {
    return transform;
  }

  public static Set<Point> getValidAdjacentPoints(Point point, int maxX, int maxY) {
    return filterPoints(
        Arrays.stream(values()).map(direction -> direction.getTransform().apply(point)),
        maxX,
        maxY
    );
  }

  public static Set<Point> getValidHorizontalOrVerticalAdjacentPoints(Point point, int maxX, int maxY) {
    return filterPoints(Stream.of(
      UP.getTransform().apply(point),
      DOWN.getTransform().apply(point),
      LEFT.getTransform().apply(point),
      RIGHT.getTransform().apply(point)
    ), maxX, maxY);
  }

  private static Set<Point> filterPoints(Stream<Point> stream, int maxX, int maxY) {
    return stream.filter(p -> (p.getX() >= 0 && p.getX() <= maxX && p.getY() >= 0 && p.getY() <= maxY))
        .collect(Collectors.toUnmodifiableSet());
  }
}