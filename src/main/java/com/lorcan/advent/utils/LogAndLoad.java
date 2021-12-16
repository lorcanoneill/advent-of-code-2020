package com.lorcan.advent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Resources;

public class Utils {

  private static final String ANSI_RESET = "\u001B[0m";

  public static String readFileAsString(String name) {
    try {
      return Resources.toString(Resources.getResource(name), StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new  RuntimeException("Could not read file " + name, ex);
    }
  }

  public static List<String> readFile(String name) {
    try {
      return Resources.readLines(Resources.getResource(name), StandardCharsets.UTF_8);
    } catch (IOException ex) {
      throw new  RuntimeException("Could not read file " + name, ex);
    }
  }

  public static void log(String format, Object... args) {
    System.out.printf(format + "\n", args);
  }

  public static void logWithoutNewLine(TextColour textColour, String format, Object... args) {
    System.out.print(
        textColour.getCode() +
        String.format(format, args) +
        ANSI_RESET
    );
  }

  public static void logWithoutNewLine(TextColour textColour, BackgroundColour backgroundColour, String format, Object... args) {
    System.out.print(
        textColour.getCode() + backgroundColour.getCode() +
        String.format(format, args) +
        ANSI_RESET
    );
  }

  public enum BackgroundColour {
    WHITE("\u001B[47m"),
    BLACK("\u001B[40m"),
    RED("\u001B[41m");

    private final String code;

    BackgroundColour(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }
  }


  public enum TextColour {
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    BLUE("\u001B[34m"),
    WHITE("\u001B[37m"),
    BLACK("\u001B[30m");

    private final String code;

    TextColour(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }
  }
}
