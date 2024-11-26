// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.gradle;

import java.io.File;
import java.util.List;

import javax.inject.Inject;

import org.gradle.api.Action;
import org.gradle.api.Project;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Nested;

/**
 * The {@link DevOpsConfig} class.
 */
public abstract class DevOpsConfig {

  private final Project project;


  public String remote;
  public String username;
  public String password;
  public String branch;


  private final ListProperty<DevOpsPipeline> pipelines;

  /**
   * Constructs an instance of {@link DevOpsConfig}.
   *
   * @param project
   */
  @Inject
  public DevOpsConfig(Project project) {
    this.project = project;
    this.pipelines = project.getObjects().listProperty(DevOpsPipeline.class).empty();
  }

  /**
   * Gets the {@link Project}.
   */
  public final Project getProject() {
    return this.project;
  }

  /**
   * Gets the working directory.
   */
  public final File getWorkingDir() {
    return this.project.getProjectDir();
  }

  @Nested
  public final List<DevOpsPipeline> getPipelines() {
    return this.pipelines.get();
  }

  public final void pipeline(Action<? super DevOpsPipeline> action) {
    this.pipelines.add(newInstance(DevOpsPipeline.class, action));
  }

  private <C> C newInstance(Class<C> clazz, Action<? super C> action) {
    C instance = this.project.getObjects().newInstance(clazz);
    action.execute(instance);
    return instance;
  }
}
