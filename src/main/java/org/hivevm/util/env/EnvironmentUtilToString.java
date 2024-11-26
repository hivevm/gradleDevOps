// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * The {@link EnvironmentUtilToString} class.
 */
abstract class EnvironmentUtilToString {

  private static final String PATTERN =
      String.format("{}%s {}={}\n", OS.isWindows() ? "set" : "export").replaceAll("\\{\\}", "%s");

  /**
   * Constructs an instance of {@link EnvironmentUtilToString}.
   */
  private EnvironmentUtilToString() {}

  /**
   * Creates the string for the {@link Environment}.
   *
   * @param environment
   */
  public static String toString(Environment environment) {
    StringBuffer buffer = new StringBuffer();
    String intent = "";
    List<Environment> list = new ArrayList<>();
    EnvironmentUtilToString.collect(environment, list);
    Map<String, String> variables = new HashMap<>();
    for (Environment e : list) {
      Map<String, String> values =
          e.toMap().entrySet().stream().filter(i -> !variables.containsKey(i.getKey()) && (i.getValue() != null))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
      for (String name : values.keySet().stream().sorted().collect(Collectors.toList())) {
        buffer.append(String.format(EnvironmentUtilToString.PATTERN, intent, name, e.get(name)));
      }
      variables.putAll(values);
      intent += "  ";
    }
    return buffer.toString();
  }

  /**
   * Collects all {@link Environment}'s.
   *
   * @param list
   */
  private static void collect(Environment env, List<Environment> list) {
    if (env instanceof EnvironmentTree) {
      EnvironmentUtilToString.collect(((EnvironmentTree) env).getDelegate(), list);
    }
    list.add(env);
  }
}
