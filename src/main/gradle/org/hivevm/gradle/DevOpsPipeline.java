// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.gradle;

import javax.inject.Inject;

import org.gradle.api.Project;

/**
 * The {@link DevOpsPipeline} class.
 */
public class DevOpsPipeline {

  private final Project project;


  public String name;


  @Inject
  public DevOpsPipeline(Project project) {
    this.project = project;
  }

  /**
   * Gets the {@link Project}.
   */
  public final Project getProject() {
    return this.project;
  }
}
