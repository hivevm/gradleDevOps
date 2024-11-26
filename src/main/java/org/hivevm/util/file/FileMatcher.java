// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.hivevm.util.env.Environment;
import org.hivevm.util.env.EnvironmentUtil;

/**
 * The {@link FileMatcher} is an utility that get all files that match the path pattern. The
 * returned {@link FileMatcher}'s allow to replace the parameters of the input with the parameters
 * found on the matches.
 */
public class FileMatcher {

  private final File        file;
  private final Environment environment;

  /**
   * Constructs an instance of {@link FileMatcher}.
   *
   * @param file
   * @param environment
   */
  FileMatcher(File file, Environment environment) {
    this.file = file;
    this.environment = environment;
  }

  /**
   * Gets the {@link File}.
   */
  public final File getFile() {
    return this.file;
  }

  /**
   * Gets the named parameter.
   */
  public final Environment getEnvironment() {
    return this.environment;
  }

  /**
   * Replaces the indexed or named placeholder's with the the parameter values.
   *
   * @param pattern
   */
  public final String map(String pattern) {
    return EnvironmentUtil.replace(pattern, this.environment);
  }

  /**
   * Get the matching {@link File} as string.
   */
  @Override
  public String toString() {
    return getFile().toString();
  }

  /**
   * Resolve the input pattern on the working directory, to find all matching files.
   *
   * @param workingDir
   * @param pattern
   */
  public static List<FileMatcher> of(File workingDir, String pattern) throws IOException {
    return FilePattern.matches(workingDir, pattern);
  }
}