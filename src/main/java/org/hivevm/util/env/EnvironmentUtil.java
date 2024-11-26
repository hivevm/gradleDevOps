// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.env;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link EnvironmentUtil} class.
 */
public abstract class EnvironmentUtil {

  private static final Pattern NAMES  = Pattern.compile("\\(\\?<([a-z][a-z_0-9]*)>", Pattern.CASE_INSENSITIVE);
  private static final Pattern PARAMS = Pattern.compile("\\$([0-9]+|[a-z][a-z_0-9]*)", Pattern.CASE_INSENSITIVE);

  /**
   * Constructs an instance of {@link EnvironmentUtil}.
   */
  private EnvironmentUtil() {}

  /**
   * Parses the group names from the pattern.
   *
   * @param pattern
   */
  public static Set<String> parseGroupNames(String pattern) {
    Set<String> names = new HashSet<>();
    Matcher matcher = EnvironmentUtil.NAMES.matcher(pattern);
    while (matcher.find()) {
      names.add(matcher.group(1));
    }
    return names;
  }

  /**
   * Replaces the indexed or named placeholder's with the the parameter values.
   *
   * @param pattern
   * @param environment
   */
  public static String replace(String pattern, Environment environment) {
    StringBuffer buffer = new StringBuffer();
    int offset = 0;

    Matcher matcher = EnvironmentUtil.PARAMS.matcher(pattern);
    while (matcher.find()) {
      String name = matcher.group(1);
      String value = environment.get(name);
      buffer.append(pattern.substring(offset, matcher.start(1) - 1));
      if (value == null) {
        buffer.append("$" + name);
      } else {
        buffer.append(value);
      }
      offset = matcher.end(1);
    }
    buffer.append(pattern.substring(offset, pattern.length()));
    return buffer.toString();
  }
}
