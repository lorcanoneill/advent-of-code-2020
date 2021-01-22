package com.lorcan.advent;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.common.io.Resources;

public class Utils {

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
}
