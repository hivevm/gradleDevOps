// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.env;

import java.util.HashMap;
import java.util.Map;

/**
 * The {@link EnvironmentMap} provides a simple map of environment variables. The is read-only, it
 * is not possible to change the values of the environment variable.
 */
class EnvironmentMap implements Environment {

  private final Map<String, String> map;

  /**
   * Constructs an instance of {@link EnvironmentMap}.
   *
   * @param map
   */
  public EnvironmentMap(Map<String, String> map) {
    this.map = map;
  }

  /**
   * <code>true</code> if the parameter is set.
   *
   * @param name
   */
  @Override
  public boolean isSet(String name) {
    return this.map.containsKey(name);
  }

  /**
   * Get a parameter by name.
   *
   * @param name
   */
  @Override
  public String get(String name) {
    return this.map.get(name);
  }

  /**
   * Get the environment variables as map.
   */
  @Override
  public Map<String, String> toMap() {
    return new HashMap<>(this.map);
  }

  /**
   * Constructs an instance of {@link Environment}.
   */
  @Override
  public final String toString() {
    return EnvironmentUtilToString.toString(this);
  }
}
