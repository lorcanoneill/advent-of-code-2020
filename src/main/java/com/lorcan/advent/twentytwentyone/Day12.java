package com.lorcan.advent.twentytwentyone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.lorcan.advent.utils.LogAndLoad;

public class Day12 {

  private static final String INPUT = "twentytwentyone/day-12-input.txt";
  private static final String SAMPLE_INPUT_1 = "twentytwentyone/day-12-sample-input-1.txt";
  private static final String SAMPLE_INPUT_2 = "twentytwentyone/day-12-sample-input-2.txt";
  private static final String SAMPLE_INPUT_3 = "twentytwentyone/day-12-sample-input-3.txt";

  private static final String START = "start";
  private static final String END = "end";

  private static final Cave START_CAVE = new Cave(START);

  public static void main(String[] args) {
    Graph<Cave> sampleInput1Graph = buildGraphFromInput(SAMPLE_INPUT_1);
    process(sampleInput1Graph, Day12::canVisitSmallCavePartOne);

    Graph<Cave> sampleInput2Graph = buildGraphFromInput(SAMPLE_INPUT_2);
    process(sampleInput2Graph, Day12::canVisitSmallCavePartOne);

    Graph<Cave> sampleInput3Graph = buildGraphFromInput(SAMPLE_INPUT_3);
    process(sampleInput3Graph, Day12::canVisitSmallCavePartOne);

    Graph<Cave> inputGraph = buildGraphFromInput(INPUT);
    process(inputGraph, Day12::canVisitSmallCavePartOne);

    Set<Path> pathsForPartTwoForInput1 = process(sampleInput1Graph, Day12::canVisitSmallCavePartTwo);

    Set<Path> expectedPathsForPartTwoForInput1 = LogAndLoad.readFile("twentytwentyone/day-12-sample-paths-1.txt")
        .stream().map(Path::fromString)
        .collect(Collectors.toUnmodifiableSet());

    for (Path path : expectedPathsForPartTwoForInput1) {
      if (!pathsForPartTwoForInput1.contains(path)) {
        LogAndLoad.log("Path that was expected not found: %s", path);
      }
    }

    process(sampleInput2Graph, Day12::canVisitSmallCavePartTwo);
    process(sampleInput3Graph, Day12::canVisitSmallCavePartTwo);
    process(inputGraph, Day12::canVisitSmallCavePartTwo);
  }

  private static Set<Path> process(Graph<Cave> graph, BiFunction<Path, Cave, Boolean> smallCaveRule) {
    int numberOfPaths = 0;
    Stack<Path> pathStack = new Stack<>();
    Set<Path> paths = new HashSet<>();

    pathStack.addAll(graph.adjacentNodes(START_CAVE).stream()
        .map(cave -> new Path(List.of(START_CAVE, cave)))
        .collect(Collectors.toUnmodifiableSet())
    );

    while (!pathStack.isEmpty()) {
      Path path = pathStack.pop();

//      Utils.log("Investigating path %s", path);
      Cave mostRecentCave = path.mostRecentCave();
      Set<Cave> adjacentCaves = graph.adjacentNodes(mostRecentCave);
//      Utils.log("Found %d adjacent caves to most recent cave %s: %s", adjacentCaves.size(), mostRecentCave, Joiner.on(", ").join(adjacentCaves));
      for (Cave adjacentCave : adjacentCaves) {
        if (adjacentCave.isEnd()) {
          numberOfPaths++;
          Path completedPath = new Path(path, adjacentCave);
//          Utils.log("Path complete: total: %d: %s", numberOfPaths, completedPath);
          paths.add(completedPath);
          continue;
        }

        if (adjacentCave.isStart()) {
          continue;
        }

        if (adjacentCave.isSmallCave()) {
          if (!smallCaveRule.apply(path, adjacentCave)) {
            continue;
          }
        }

        Path updatedPath = new Path(path, adjacentCave);
//        Utils.log("Adding path to stack: %s", updatedPath);
        pathStack.add(updatedPath);
      }
    }
    LogAndLoad.log("Number of paths: %d", numberOfPaths);
    return paths;
  }

  private static boolean canVisitSmallCavePartOne(Path path, Cave cave) {
    return !path.hasSeenCave(cave);
  }
  private static boolean canVisitSmallCavePartTwo(Path path, Cave cave) {
    long howManyTimesHasSeenCave = path.howManyTimesHasSeenCave(cave);
    if (howManyTimesHasSeenCave == 0) {
      return true;
    }

    return !path.getHasSeenSingleCaveTwice();
  }

  private static Graph<Cave> buildGraphFromInput(String input) {
    List<String> connections = LogAndLoad.readFile(input);
    MutableGraph<Cave> mutableGraph = GraphBuilder.undirected()
        .allowsSelfLoops(false)
        .build();

    Set<String> nodeIdentifiers = new HashSet<>();
    for (String connection : connections) {
      nodeIdentifiers.addAll(Arrays.asList(connection.split("-")));
    }

    for (String identifier : nodeIdentifiers) {
      mutableGraph.addNode(new Cave(identifier));
    }

    for (String connection : connections) {
      String[] arr = connection.split("-");
      mutableGraph.putEdge(new Cave(arr[0]), new Cave(arr[1]));
    }

    return mutableGraph;
  }

  static class Path {
    private final List<Cave> caves;
    private final Set<String> smallCaveIdentifiers;
    private boolean hasSeenSingleCaveTwice = false;

    public Path(Path path, Cave cave) {
      this.caves = new ArrayList<>();
      this.smallCaveIdentifiers = new HashSet<>();
      for (Cave c : path.getCaves()) {
        if (c.isSmallCave()) {
          if (smallCaveIdentifiers.contains(c.getIdentifier())) {
            hasSeenSingleCaveTwice = true;
          }
          smallCaveIdentifiers.add(c.getIdentifier());
        }
        this.caves.add(c);
      }

      if (cave.isSmallCave()) {
        if (smallCaveIdentifiers.contains(cave.getIdentifier())) {
          hasSeenSingleCaveTwice = true;
        }
      }
      this.caves.add(cave);
    }

    public Path(List<Cave> caves) {
      this.caves = new ArrayList<>();
      this.smallCaveIdentifiers = new HashSet<>();
      for (Cave cave : caves) {
        if (cave.isSmallCave()) {
          this.smallCaveIdentifiers.add(cave.getIdentifier());
        }

        this.caves.add(cave);
      }
    }

    public List<Cave> getCaves() {
      return caves;
    }

    public Cave mostRecentCave() {
      return Iterables.getLast(caves);
    }

    public boolean hasSeenCave(Cave cave) {
      return this.caves.contains(cave);
    }

    public long howManyTimesHasSeenCave(Cave cave) {
      return caves.stream()
          .filter(c -> c.getIdentifier().equals(cave.getIdentifier()))
          .count();
    }

    public boolean getHasSeenSingleCaveTwice() {
      return hasSeenSingleCaveTwice;
    }

    @Override
    public int hashCode() {
      return Objects.hash(caves);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Path)) {
        return false;
      }

      Path that = (Path) obj;
      return this.getCaves().equals(that.getCaves());
    }

    @Override
    public String toString() {
      return Joiner.on(",").join(caves);
    }

    public static Path fromString(String str) {
      return new Path(Arrays.stream(str.split(",")).map(Cave::new).collect(Collectors.toUnmodifiableList()));
    }
  }

  static class Cave {
    private final String identifier;

    public Cave(String identifier) {
      this.identifier = identifier;
    }

    public String getIdentifier() {
      return identifier;
    }

    public boolean isStart() {
      return identifier.equals(START);
    }

    public boolean isEnd() {
      return identifier.equals(END);
    }

    public boolean isSmallCave() {
      return identifier.toLowerCase().equals(identifier);
    }

    @Override
    public int hashCode() {
      return Objects.hash(identifier);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Cave)) {
        return false;
      }

      Cave that = (Cave) obj;
      return this.getIdentifier().equals(that.getIdentifier());
    }

    @Override
    public String toString() {
      return identifier;
    }
  }
}
