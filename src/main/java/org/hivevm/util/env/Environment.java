// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.env;

import java.util.Collections;
import java.util.Map;

/**
 * The {@link Environment} provides the environment variables for an execution context. Depends of
 * the {@link Environment} implementation if the variables are available in read-only mode or in
 * write mode.
 */
public interface Environment {

  /**
   * Return <code>true</code> if the parameter is set.
   *
   * @param name
   */
  boolean isSet(String name);

  /**
   * Get a parameter by name.
   *
   * @param name
   */
  String get(String name);

  /**
   * Get the environment variables as map.
   */
  Map<String, String> toMap();

  /**
   * Creates a new {@link Environment} adding the {@link Map} for fallback's.
   *
   * @param map
   */
  default Environment map(Map<String, String> map) {
    return new EnvironmentTree(map, this);
  }

  /**
   * Creates an empty {@link Environment}.
   */
  static Environment empty() {
    return Environment.of(Collections.emptyMap());
  }

  /**
   * Creates an {@link Environment} from the system environment.
   */
  static Environment system() {
    return Environment.of(System.getenv());
  }

  /**
   * Creates an {@link Environment} for the map.
   *
   * @param map
   */
  static Environment of(Map<String, String> map) {
    return new EnvironmentMap(map);
  }
}
