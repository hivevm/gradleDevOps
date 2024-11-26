// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.ExtensionContainer;

/**
 * The {@link DevOpsPlugin} defines the different tasks required for a smart.IO build management.
 */
public class DevOpsPlugin implements Plugin<Project> {

  private static final String CONFIG = "gradleDevOps";

  @Override
  public void apply(Project project) {
    ExtensionContainer extension = project.getExtensions();
    extension.create(DevOpsPlugin.CONFIG, DevOpsConfig.class);

    // project.getTasks().register("generateParser", ParserGenerator.class);
    project.getTasks().register("run", DevOpsTask.class);
  }
}