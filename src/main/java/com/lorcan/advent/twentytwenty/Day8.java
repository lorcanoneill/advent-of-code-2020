package com.lorcan.advent.twentytwenty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.lorcan.advent.utils.LogAndLoad;

public class Day8 {

  public static void main(String[] args) {
    AtomicInteger atomicInteger = new AtomicInteger(0);
    List<Instruction> instructions = LogAndLoad.readFile("twentytwenty/day-8-input.txt")
        .stream()
        .map(str -> Instruction.fromString(str, atomicInteger))
        .collect(Collectors.toUnmodifiableList());
    partOne(instructions);
    partTwo(instructions);
  }

  private static void partOne(List<Instruction> instructions) {
    int accumulator = 0;
    int pointer = 0;
    Set<Integer> idsSeen = new HashSet<>();
    while (true) {
      Instruction instruction = instructions.get(pointer);
      if (idsSeen.contains(instruction.getId())) {
        break;
      }

      idsSeen.add(instruction.getId());

      switch (instruction.getOperation()) {
        case ACC:
          accumulator += instruction.getArgument();
          pointer++;
          break;
        case JMP:
          pointer += instruction.getArgument();
          break;
        case NOP:
          pointer++;
          break;
      }
    }

    System.out.format("Found repeated instruction at pointer %d: accumulator: %d, number of instructions executed: %d\n",
      pointer, accumulator, idsSeen.size()
    );


  }

  private static void partTwo(List<Instruction> instructions) {
    List<Instruction> alternativeInstructions = new ArrayList<>();
    for (Instruction instruction : instructions) {
      switch (instruction.getOperation()) {
        case ACC:
          continue;
        case JMP:
          alternativeInstructions.add(instruction.withOperation(Operation.NOP));
          break;
        case NOP:
          alternativeInstructions.add(instruction.withOperation(Operation.JMP));
          break;
      }
    }

    // find out which instruction will enable us to terminate
    for (Instruction instruction : alternativeInstructions) {
      if (canTerminateFromThisInstruction(instruction, instructions)) {
        int idOfInstructionWeNeedToChange = instruction.getId();
        Instruction matchingInstruction = instructions.stream().filter(i -> i.getId() == instruction.getId()).findAny().get();
        int initialPointer = instructions.indexOf(matchingInstruction);
        System.out.format("Trying to replace instruction with id %d at pointer %d with operation %s\n", idOfInstructionWeNeedToChange, initialPointer, instruction.getOperation());

        int accumulator = 0;
        int pointer = 0;
        Set<Integer> idsSeen = new HashSet<>();
        boolean terminated = false;
        while (true) {
          if (pointer >= instructions.size() - 1) {
            terminated = true;
            break;
          }

          Instruction thisInstruction = instructions.get(pointer);
          Operation operation = thisInstruction.getId() == idOfInstructionWeNeedToChange
              ? instruction.getOperation()
              : thisInstruction.getOperation();

          if (idsSeen.contains(thisInstruction.getId())) {
            break;
          }

          idsSeen.add(thisInstruction.getId());

          switch (operation) {
            case ACC:
              accumulator += thisInstruction.getArgument();
              pointer++;
              break;
            case JMP:
              pointer += thisInstruction.getArgument();
              break;
            case NOP:
              pointer++;
              break;
          }
        }

        if (terminated) {
          System.out.format("Success: this program terminated successfully when we changed the instruction at pointer %d to %s: accumulator: %d\n", initialPointer, instruction.getOperation(), accumulator);
          break;
        } else {
          System.out.format("Alas: this program did not terminate successfully when we changed the instruction at pointer %d to %s\n", initialPointer, instruction.getOperation());
        }
      }
    }
  }

  private static boolean canTerminateFromThisInstruction(Instruction instruction, List<Instruction> instructions) {
    Instruction matchingInstruction = instructions.stream().filter(i -> i.getId() == instruction.getId()).findAny().get();
    int pointer = instructions.indexOf(matchingInstruction);
    Set<Integer> idsSeen = new HashSet<>();
    boolean terminated = false;
    while (true) {
      if (pointer >= instructions.size() - 1) {
        terminated = true;
        break;
      }

      Instruction thisInstruction = instructions.get(pointer);
      Operation operation = thisInstruction.getId() == instruction.getId()
          ? instruction.getOperation()
          : thisInstruction.getOperation();

      if (idsSeen.contains(thisInstruction.getId())) {
        break;
      }

      idsSeen.add(thisInstruction.getId());

      switch (operation) {
        case ACC:
        case NOP:
          pointer++;
          break;
        case JMP:
          pointer += thisInstruction.getArgument();
          break;
      }
    }

    return terminated;
  }

  static class Instruction {
    private final int id;
    private final Operation operation;
    private final int argument;

    private Instruction(int id, Operation operation, int argument) {
      this.id = id;
      this.operation = operation;
      this.argument = argument;
    }

    public int getId() {
      return id;
    }

    public Operation getOperation() {
      return operation;
    }

    public int getArgument() {
      return argument;
    }

    public Instruction withOperation(Operation operation) {
      return new Instruction(this.id, operation, this.argument);
    }

    public static Instruction fromString(String str, AtomicInteger atomicInteger) {
      String[] arr = str.split(" ");
      return new Instruction(
          atomicInteger.incrementAndGet(),
          Operation.fromString(arr[0]),
          Integer.parseInt(arr[1], 10)
      );
    }
  }

  enum Operation {
    ACC,
    JMP,
    NOP;

    public static Operation fromString(String str) {
      return Operation.valueOf(str.toUpperCase());
    }
  }
}
