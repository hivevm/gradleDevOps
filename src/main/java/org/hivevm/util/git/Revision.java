// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hivevm.util.Version;

/**
 * The {@link Revision} is a utility to fetch version information from the GIT repository.
 */
public class Revision extends Version {

  private static final Pattern           BUILDNUMBER     = Pattern.compile("^[^\\d]*(\\d+)$");
  private static final DateTimeFormatter OFFSET_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-mm hh:mm:ss xx");

  private final String                   hash;
  private final OffsetDateTime           time;

  /**
   * Constructs an instance of {@link Revision}.
   *
   * @param hash
   * @param time
   * @param version
   */
  public Revision(String hash, OffsetDateTime time, Version version) {
    super(version.getMajor(), version.getMinor(), version.getPatch(), version.getName(), version.getBuild());
    this.hash = hash;
    this.time = time;
  }

  /**
   * Gets the commit hash.
   */
  public final String getHash() {
    return this.hash;
  }

  /**
   * Gets the commit {@link OffsetDateTime}.
   */
  public final OffsetDateTime getTime() {
    return this.time;
  }

  /**
   * Gets the commit date/time as ISO format.
   */
  public final String getISOTime() {
    return this.time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }

  /**
   * Gets the commit date/time as basic ISO format.
   */
  public final String getSimpleTime() {
    return this.time.format(Revision.OFFSET_DATETIME);
  }

  /**
   * Extracts the build number from the build text.
   */
  public final long getBuildNumber() {
    Matcher matcher = Revision.BUILDNUMBER.matcher(getBuild());
    return matcher.find() ? Long.parseLong(matcher.group(1)) : 0;
  }

  /**
   * Gets the hash of the commit.
   */
  public final String getRelease() {
    return toString("0.0");
  }

  /**
   * Gets the version.
   */
  public final String getVersion() {
    return toString("0.0.0");
  }

  /**
   * Creates a new instance of {@link Revision} using the {@link Version}.
   *
   * @param version
   */
  public final Revision build(Version version) {
    return new Revision(getHash(), getTime(), version);
  }
}
