package com.lorcan.advent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

public class Day17 {

  public static void main(String[] args) {
    List<String> lines = Utils.readFile("day-17-sample-input.txt");
    List<Cube> cubes = instantiateCells(lines);
    partOne(cubes);
  }

  private static void partOne(List<Cube> cubes) {
    List<Cube> result = new ArrayList<>();
    result.addAll(cubes);
    for (int i = 1; i <= 6; i++) {
      List<Cube> theseCubes = new ArrayList<>();
      theseCubes.addAll(result);

      int pointer = 0;
      for (Cube cube : result) {
        if (++pointer % 10000 == 0) {
          System.out.format("Progress: processed %d from %d on initial pass during iteration %d\n", pointer, result.size(), i);
        }

        for (Coordinates neighbouringCellCoordinates : cube.getNeighbouringCellCoordinates()) {
          Optional<Cube> maybeExistingCell = result.stream().filter(
              c -> c.getCoordinates().matches(neighbouringCellCoordinates)
          ).findAny();

          if (maybeExistingCell.isEmpty()) {
            maybeExistingCell = theseCubes.stream().filter(
                c -> c.getCoordinates().matches(neighbouringCellCoordinates)
            ).findAny();

            if (maybeExistingCell.isEmpty()) {
              theseCubes.add(new Cube(
                  neighbouringCellCoordinates, State.INACTIVE
              ));
            }
          } else {
            theseCubes.add(maybeExistingCell.get());
          }
        }
      }

      pointer = 0;
      // build a new set of cubes applying the transforms
      List<Cube> transformedCubes = new ArrayList<>();
      for (Cube cube : result) {
        if (++pointer % 10000 == 0) {
          System.out.format("Progress: processed %d from %d on transform pass during iteration %d\n", pointer, result.size(), i);
        }

        long countOfActiveNeighbours = 0;
        for (Coordinates coordinates : cube.getNeighbouringCellCoordinates()) {
          Optional<Cube> maybeCube = theseCubes.stream().filter(c1 -> c1.getCoordinates().matches(coordinates)).findAny();
          if (maybeCube.isEmpty()) {
            throw new UnsupportedOperationException(String.format("Could not find cube at coordinates " + coordinates));
          }

          if (maybeCube.get().getState() == State.ACTIVE) {
            countOfActiveNeighbours++;
          }
        }

        if (cube.getState() == State.ACTIVE) {
          if (countOfActiveNeighbours == 2 || countOfActiveNeighbours == 3) {
            transformedCubes.add(cube);
          } else {
            transformedCubes.add(cube.withState(State.INACTIVE));
          }
        } else {
          if (countOfActiveNeighbours == 3) {
            transformedCubes.add(cube.withState(State.ACTIVE));
          } else {
            transformedCubes.add(cube);
          }
        }
      }



      result = transformedCubes;
      result.addAll(theseCubes);
    }

    long countOfActiveCells = result.stream()
        .filter(c -> c.getState() == State.ACTIVE)
        .count();

    System.out.format("Count of active cubes: %d\n", countOfActiveCells);
  }

  private static List<Cube> instantiateCells(List<String> lines) {
    ImmutableList.Builder<Cube> builder = ImmutableList.builder();
    for (int x = 0; x < lines.size(); x++) {
      String line = lines.get(x);
      for (int y = 0; y < line.length(); y++) {
        builder.add(new Cube(
            Coordinates.of(x, y,0),
            State.fromChar(line.charAt(y))
        ));
      }
    }

    return builder.build();
  }

  static class Cubes {
    private final List<Cube> cubes;

    Cubes(List<Cube> cubes) {
      this.cubes = cubes;
    }

    public
  }

  static class Coordinates {
    private final int x;
    private final int y;
    private final int z;

    Coordinates(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }

    public int getX() {
      return x;
    }

    public int getY() {
      return y;
    }

    public int getZ() {
      return z;
    }

    public boolean matches(Coordinates c) {
      return getX() == c.getX()
          && getY() == c.getY()
          && getZ() == c.getZ();
    }

    @Override
    public int hashCode() {
      return Objects.hash(x, y, z);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Coordinates)) {
        return false;
      }

      Coordinates that = (Coordinates) obj;
      return this.getX() == that.getX()
          && this.getY() == that.getY()
          && this.getZ() == that.getZ();
    }

    @Override
    public String toString() {
      return String.format("x = %d, y = %d, z = %d", x, y, z);
    }

    public static Coordinates of(int x, int y, int z) {
      return new Coordinates(x, y, z);
    }
  }

  static class Cube {
    private final Coordinates coordinates;
    private final State state;

    Cube(Coordinates coordinates, State state) {
      this.coordinates = coordinates;
      this.state = state;
    }

    public Coordinates getCoordinates() {
      return coordinates;
    }

    public State getState() {
      return state;
    }

    public Cube withState(State state) {
      return new Cube(coordinates, state);
    }

    @Override
    public String toString() {
      return String.format("coordinates = %s, state = %s", coordinates, state);
    }

    public List<Coordinates> getNeighbouringCellCoordinates() {
      Coordinates c = getCoordinates();

      return List.of(
          Coordinates.of(c.getX() - 1, c.getY() - 1, c.getZ() - 1),
          Coordinates.of(c.getX() - 1, c.getY(), c.getZ() - 1),
          Coordinates.of(c.getX() - 1, c.getY() + 1, c.getZ() - 1),
          Coordinates.of(c.getX() - 1, c.getY() - 1, c.getZ()),
          Coordinates.of(c.getX() - 1, c.getY(), c.getZ()),
          Coordinates.of(c.getX() - 1, c.getY() + 1, c.getZ()),
          Coordinates.of(c.getX() - 1, c.getY() - 1, c.getZ() + 1),
          Coordinates.of(c.getX() - 1, c.getY(), c.getZ() + 1),
          Coordinates.of(c.getX() - 1, c.getY() + 1, c.getZ() + 1),
          Coordinates.of(c.getX(), c.getY() - 1, c.getZ() - 1),
          Coordinates.of(c.getX(), c.getY() - 1, c.getZ()),
          Coordinates.of(c.getX(), c.getY() - 1, c.getZ() + 1),
          Coordinates.of(c.getX(), c.getY(), c.getZ() - 1),
          Coordinates.of(c.getX(), c.getY(), c.getZ() + 1),
          Coordinates.of(c.getX(), c.getY() + 1, c.getZ() - 1),
          Coordinates.of(c.getX(), c.getY() + 1, c.getZ()),
          Coordinates.of(c.getX(), c.getY() + 1, c.getZ() + 1),
          Coordinates.of(c.getX() + 1, c.getY() - 1, c.getZ() - 1),
          Coordinates.of(c.getX() + 1, c.getY() - 1, c.getZ()),
          Coordinates.of(c.getX() + 1, c.getY() - 1, c.getZ() + 1),
          Coordinates.of(c.getX() + 1, c.getY(), c.getZ() - 1),
          Coordinates.of(c.getX() + 1, c.getY(), c.getZ()),
          Coordinates.of(c.getX() + 1, c.getY(), c.getZ() + 1),
          Coordinates.of(c.getX() + 1, c.getY() + 1, c.getZ() - 1),
          Coordinates.of(c.getX() + 1, c.getY() + 1, c.getZ()),
          Coordinates.of(c.getX() + 1, c.getY() + 1, c.getZ() + 1)
      );
    }
  }

  enum State {
    ACTIVE,
    INACTIVE;

    public static State fromChar(char c) {
      if (c == '.') {
        return State.INACTIVE;
      }

      return State.ACTIVE;
    }
  }
}
