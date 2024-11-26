// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.env;

import java.util.Map;

/**
 * The {@link EnvironmentTree} class.
 */
class EnvironmentTree extends EnvironmentMap {

  private final Environment parent;

  /**
   * Constructs an instance of {@link EnvironmentTree}.
   *
   * @param map
   * @param parent
   */
  public EnvironmentTree(Map<String, String> map, Environment parent) {
    super(map);
    this.parent = parent;
  }

  /**
   * Gets the parent {@link Environment}.
   */
  protected final Environment getDelegate() {
    return this.parent;
  }

  /**
   * Return <code>true</code> if the parameter is set.
   *
   * @param name
   */
  @Override
  public final boolean isSet(String name) {
    return super.isSet(name) || getDelegate().isSet(name);
  }

  /**
   * Get a parameter by name.
   *
   * @param name
   */
  @Override
  public String get(String name) {
    return super.isSet(name) ? super.get(name) : getDelegate().get(name);
  }

  /**
   * Get the environment variables as map.
   */
  @Override
  public Map<String, String> toMap() {
    Map<String, String> variables = getDelegate().toMap();
    variables.putAll(super.toMap());
    return variables;
  }
}
