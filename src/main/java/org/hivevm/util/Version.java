// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util;

import java.util.regex.Pattern;

/**
 * The {@link Version} implements the semantic version syntax (https://semver.org/spec/v2.0.0.html).
 * Depending on the software the major.minor.patch are interpreted differently.
 *
 * On the API the interpretation of the version number is following:
 *
 * <pre>
 * - MAJOR version when you make incompatible API changes,
 * - MINOR version when you add functionality in a backwards compatible manner, and
 * - PATCH version when you make backwards compatible bug fixes.
 * </pre>
 *
 * On a released client software the version number is interpreted as following:
 *
 * <pre>
 * - MAJOR defines the year of release,
 * - MINOR defines the month of release
 * - PATCH version when you make backwards compatible bug fixes.
 * </pre>
 *
 * For the interpretation of a full version text see the Backus–Naur Form Grammar from the
 * specification.
 *
 * E.g.:
 *
 * <pre>
 *   19.12
 *   19.12.1
 *   19.12.1-rc1
 *   19.12-beta1+build.1.2
 *   19.04
 *   19.4+build.1.2
 * </pre>
 */
public class Version implements Comparable<Version> {

  public static final Version NONE = new Version(0, 0, 0, null, null);


  private static final String  PATTERN =
      "(?<major>\\d+)\\.(?<minor>\\d+)(?:\\.(?<patch>\\d+))?(?:-(?<name>[a-zA-Z0-9.]+))?(?:\\+(?<build>[a-zA-Z0-9.]+))?";

  private static final Pattern PARSE   = Pattern.compile(Version.PATTERN);
  private static final Pattern MATCH   = Pattern.compile("^" + Version.PATTERN + "$");
  private static final Pattern FORMAT  = Pattern.compile("([0]+)\\.([0]+)(?:\\.([0]+))?(?:-([0]+))?(?:\\+([0]+))?");


  private final int    major;
  private final int    minor;
  private final int    patch;

  private final String name;
  private final String build;

  /** Constructs a new {@code Version} instance with the specified version details. */
  protected Version(int major, int minor, int patch, String name, String build) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.name = name;
    this.build = build;
  }

  /** Retrieves the major version component of this {@code Version} instance. */
  public final int major() {
    return this.major;
  }

  /** Retrieves the minor version component of this {@link Version} instance. */
  public final int minor() {
    return this.minor;
  }

  /** Retrieves the patch version of the {@link Version} object. */
  public final int patch() {
    return this.patch;
  }

  /** Retrieves the pre-release name associated with the version. */
  public final String name() {
    return this.name;
  }

  /** Gets the build information associated with the version. */
  public final String build() {
    return this.build;
  }

  /**
   * Compares this {@link Version} object with the specified {@link Version} object for order.
   *
   * The comparison is performed by comparing the major, minor, and patch version components
   * in that order. A higher version component results in a lower ranking (e.g., higher versions
   * come before lower versions in the natural order).
   */
  @Override
  public int compareTo(Version other) {
    if (major() != other.major()) // Major version
      return major() > other.major() ? -1 : 1;
    else if (minor() != other.minor()) // Minor version
      return minor() > other.minor() ? -1 : 1;
    else if (patch() != other.patch()) // Patch version
      return patch() > other.patch() ? -1 : 1;
    return 0;
  }

  /**
   * Compares this {@link Version} object to the specified object for equality.
   * Two {@link Version} objects are considered equal if the specified object is
   * an instance of {@link Version} and their comparison returns zero.
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    return (obj instanceof Version) && (compareTo((Version) obj) == 0);
  }

  /** Creates a new {@link Version} instance by adding the specified build text to the existing version. */
  public final Version build(String build) {
    return new Version(major(), minor(), patch(), name(), build);
  }

  /** Creates a new instance of {@link Version} using the provided numeric build number. */
  public final Version build(long build) {
    return build("" + build);
  }

  /** Creates a new instance of {@link Version} with the specified pre-release name. */
  public final Version preRelease(String name) {
    return new Version(major(), minor(), patch(), name, build());
  }

  /**
   * Returns a string representation of the version, including the major, minor,
   * and optionally the patch, pre-release name, and build information.
   *
   * The output format is as follows:
   * - [major].[minor] (always included)
   * - Optionally adds the patch (if specified) as [major].[minor].[patch].
   * - Includes a pre-release name (if specified) as [major].[minor].[patch]-[name].
   * - Adds build information (if specified) as [major].[minor].[patch]-[name]+[build].
   */
  @Override
  public final String toString() {
    var buffer = new StringBuffer();
    buffer.append(major());
    buffer.append(".");
    buffer.append(minor());
    if (patch() > -1) {
      buffer.append(".");
      buffer.append(patch());
    }
    if (name() != null) {
      buffer.append("-");
      buffer.append(name());
    }
    if (build() != null) {
      buffer.append("+");
      buffer.append(build());
    }
    return buffer.toString();
  }

  /**
   * Returns a formatted string representation of the version based on the specified format.
   *
   * The format string allows customization of the version string's structure, including the number of
   * digits for major, minor, and patch versions, as well as including optional pre-release and build
   * identifiers when present.
   */
  public final String toString(String format) {
    var matcher = Version.FORMAT.matcher(format);
    if (!matcher.find())
      return toString();

    var text = "%0" + matcher.group(1).length() + "d.%0" + matcher.group(2).length() + "d";
    var buffer = new StringBuffer();
    buffer.append(String.format(text, major(), minor()));
    if (matcher.group(3) != null) {
      text = ".%0" + matcher.group(3).length() + "d";
      buffer.append(String.format(text, patch() < 0 ? 0 : patch()));
    }
    if ((matcher.group(4) != null) && (name() != null)) {
      buffer.append("-");
      buffer.append(name());
    }
    if ((matcher.group(5) != null) && (build() != null)) {
      buffer.append("+");
      buffer.append(build());
    }
    return buffer.toString();
  }

  /** Creates a new {@link Version} instance with the specified major and minor version numbers. */
  public static Version of(int major, int minor) {
    return Version.of(major, minor, -1, null, null);
  }

  /** Creates a new instance of {@link Version} using the provided major, minor, and patch numbers. */
  public static Version of(int major, int minor, int patch) {
    return Version.of(major, minor, patch, null, null);
  }

  /**
   * Creates a new instance of {@link Version} with the specified major, minor,
   * pre-release name, and build identifier.
   */
  public static Version of(int major, int minor, String pre, String build) {
    return Version.of(major, minor, -1, pre, build);
  }

  /** Creates a new instance of {@link Version}. */
  public static Version of(int major, int minor, int patch, String pre, String build) {
    return new Version(major, minor, patch, pre, build);
  }

  /** Creates a {@link Version} instance by parsing the given version string. */
  public static Version of(String text) throws IllegalArgumentException {
    return Version.parse(text, Version.MATCH);
  }

  /**
   * Parses a version string and returns a {@link Version} object.
   *
   * This method uses a predefined parsing pattern to interpret the input string
   * into distinct components such as major, minor, patch, pre-release name,
   * and build version. If the input string does not conform to the expected
   * format, an exception is thrown.
   */
  public static Version parse(String text) throws IllegalArgumentException {
    return Version.parse(text, Version.PARSE);
  }

  /**
   * Parses a version string using the given pattern and returns a {@link Version} object.
   *
   * The method attempts to match the provided version string with the given regular expression
   * pattern. If a valid match is found, the method extracts components such as major, minor, patch,
   * pre-release name, and build version from the matched groups and creates a {@link Version}
   * instance.
   */
  public static Version parse(String text, Pattern pattern) throws IllegalArgumentException {
    if (text == null)
      return null;

    var matcher = pattern.matcher(text);
    if (!matcher.find())
      throw new IllegalArgumentException("'" + text + "' is not a valid version");

    var major = Integer.parseInt(matcher.group("major"));
    var minor = Integer.parseInt(matcher.group("minor"));
    var patch = (matcher.group("patch") == null) ? -1 : Integer.parseInt(matcher.group("patch"));
    return Version.of(major, minor, patch, matcher.group("name"), matcher.group("build"));
  }
}