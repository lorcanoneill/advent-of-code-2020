package com.lorcan.advent.twentytwentyone;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.lorcan.advent.utils.Direction;
import com.lorcan.advent.utils.LogAndLoad;
import com.lorcan.advent.utils.Point;

public class Day15 {

  private static final String INPUT = "twentytwentyone/day-15-input.txt";
  private static final String SAMPLE_INPUT = "twentytwentyone/day-15-sample-input.txt";

  public static void main(String[] args) {
    List<String> lines = LogAndLoad.readFile(INPUT);
    int maxY = lines.size();
    int maxX = lines.get(0).length();
    int[][] riskLevels = new int[maxY][maxX];
    for (int y = 0; y < maxY; y++) {
      String line = lines.get(y);
      for (int x = 0; x < maxX; x++) {
        riskLevels[y][x] = Integer.parseInt(line.substring(x, x + 1));
      }
    }

    partOne(riskLevels);
  }

  private static void partOne(int[][] riskLevels) {
    int goalX = riskLevels[0].length - 1;
    int goalY = riskLevels.length - 1;

    AtomicLong maxRiskLevel = new AtomicLong(Long.MAX_VALUE);

    Deque<Path> deque = new ArrayDeque<>();
    Point endPoint = new Point(goalX, goalY);
    Direction.getValidHorizontalOrVerticalAdjacentPoints(endPoint, goalX, goalY)
        .stream()
        .map(p -> new Path(List.of(endPoint, p), riskLevels[p.getY()][p.getX()]))
        .forEach(deque::add);

    Stopwatch stopwatch = Stopwatch.createStarted();

    Executor executor = Executors.newFixedThreadPool(1000);

    while (!deque.isEmpty()) {
      Runnable runnable = () -> {
        Path path = deque.pop();
        if (path.getTotalRiskLevel() >= maxRiskLevel.get()) {
          return;
        }

        if (stopwatch.elapsed(TimeUnit.MILLISECONDS) % 5000 == 0) {
          LogAndLoad.log("%dms: paths: %d, checking path: %s", stopwatch.elapsed(TimeUnit.MILLISECONDS), deque.size(), path);
        }

        Point mostRecentPoint = path.getMostRecentPoint();

        List<Point> pointsSortedByRiskLevelAscending = Sets.difference(
                Direction.getValidHorizontalOrVerticalAdjacentPoints(mostRecentPoint, goalX, goalY),
                Set.copyOf(path.getPoints())
            )
            .stream()
            .sorted(Comparator.comparingInt(p -> -riskLevels[p.getY()][p.getX()]))
            .collect(Collectors.toUnmodifiableList());

        for (Point p : pointsSortedByRiskLevelAscending) {
          int riskLevel = riskLevels[p.getY()][p.getX()];
          if (p.getX() == 0 && p.getY() == 0) {
            int riskLevelForPath = path.getTotalRiskLevel() + riskLevel;
            if (riskLevelForPath < maxRiskLevel.get()) {
              LogAndLoad.log("Completed path with total risk level %d from %d points", riskLevelForPath, path.getPoints().size() + 1);
              maxRiskLevel.set(riskLevelForPath);
            }
            continue;
          }

          deque.add(new Path(path, p, riskLevel));
        }
      };

      runnable.run();

//      if (deque.size() < 100) {
//        runnable.run();
//      } else {
//        CompletableFuture.runAsync(runnable, executor);
//      }
    }

    LogAndLoad.log("Stack empty. Risk level: %d", maxRiskLevel.get());
  }

  static class Path {
    private final List<Point> points = new ArrayList<>();
    private int totalRiskLevel = 0;

    Path(List<Point> points, int riskLevel) {
      this.points.addAll(points);
      this.totalRiskLevel = riskLevel;
    }

    Path(Path path, Point point, int riskLevel) {
      this.points.addAll(path.getPoints());
      this.points.add(point);
      this.totalRiskLevel = path.getTotalRiskLevel() + riskLevel;
    }

    public List<Point> getPoints() {
      return points;
    }

    public Point getMostRecentPoint() {
      return Iterables.getLast(points);
    }

    public int getTotalRiskLevel() {
      return totalRiskLevel;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      Path path = (Path) o;
      return totalRiskLevel == path.totalRiskLevel && Objects.equals(points, path.points);
    }

    @Override
    public int hashCode() {
      return Objects.hash(points, totalRiskLevel);
    }

    @Override
    public String toString() {
      return String.format("%s, r: %d", getMostRecentPoint(), getTotalRiskLevel());
    }
  }
}
