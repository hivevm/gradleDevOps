// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.env;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * The {@link EnvironmentVariables} provides additional environment variables.
 */
public class EnvironmentVariables implements Environment {

  private final Map<String, String> variables;
  private final Environment         environment;

  /**
   * Constructs an instance of {@link EnvironmentVariables}.
   *
   * @param variables
   * @param environment
   */
  public EnvironmentVariables(Map<String, String> variables, Environment environment) {
    this.variables = variables;
    this.environment = environment;
  }

  /**
   * Returns the names of all variables.
   */
  public final Set<String> getVariables() {
    return this.variables.keySet();
  }

  /**
   * Get the local variable by name.
   *
   * @param name
   */
  public final String getVariable(String name) {
    return this.variables.get(name);
  }

  /**
   * Set the local variable by name.
   *
   * @param name
   */
  public final void setVariable(String name, String value) {
    this.variables.put(name, value);
    this.variables.put(name, value);
  }

  /**
   * Returns true if the environment contains the named variable.
   *
   * @param name
   */
  @Override
  public final boolean isSet(String name) {
    return this.variables.containsKey(name) || this.environment.isSet(name);
  }

  /**
   * Returns the named environment variable, throws an exception otherwise.
   *
   * @param name
   */
  @Override
  public final String get(String name) {
    return this.variables.containsKey(name) ? this.variables.get(name) : this.environment.get(name);
  }

  /**
   * Converts the environment variables as {@link Map}.
   */
  @Override
  public final Map<String, String> toMap() {
    Map<String, String> map = new HashMap<>(this.environment.toMap());
    map.putAll(this.variables);
    return map;
  }


  private Map<String, String> toVariables() {
    Map<String, String> vars =
        (this.environment instanceof EnvironmentVariables) ? ((EnvironmentVariables) this.environment).toVariables()
            : new HashMap<>();
    vars.putAll(this.variables);
    return this.variables;
  }

  /**
   * Creates a string of the local defined environment variables.
   */
  @Override
  public final String toString() {
    Map<String, String> vars = toVariables();
    return vars.isEmpty() ? ""
        : "\n" + vars.keySet().stream().sorted().map(k -> String.format("  %s\t= %s", k, vars.get(k)))
            .collect(Collectors.joining("\n"));
  }
}
