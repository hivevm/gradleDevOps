// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.hivevm.util.Version;

/**
 * Represents a specific revision of a software version, which extends the basic properties of a
 * {@link Version} class with additional metadata such as a commit hash and timestamp.
 */
public class Revision extends Version {

    private static final Pattern           BUILDNUMBER     = Pattern.compile("^[^\\d]*(\\d+)$");
    private static final DateTimeFormatter OFFSET_DATETIME = DateTimeFormatter.ofPattern(
        "yyyy-MM-mm hh:mm:ss xx");

    private final String         hash;
    private final OffsetDateTime time;

    /**
     * Creates a new instance of the {@code Revision} class, representing a specific revision of a
     * software version, with an associated commit hash, timestamp, and version details.
     */
    public Revision(String hash, OffsetDateTime time, Version version) {
        super(version.major(), version.minor(), version.patch(), version.name(), version.build());
        this.hash = hash;
        this.time = time;
    }

    /**
     * Retrieves the commit hash associated with this revision.
     */
    public final String hash() {
        return this.hash;
    }

    /**
     * Retrieves the timestamp associated with this revision.
     */
    public final OffsetDateTime time() {
        return this.time;
    }

    /**
     * Retrieves the timestamp associated with this revision in ISO-8601 format, including the
     * offset from UTC/Greenwich.
     */
    public final String isoTime() {
        return this.time.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    /**
     * Retrieves the timestamp associated with this revision in a simplified date-time format.
     */
    public final String simpleTime() {
        return this.time.format(Revision.OFFSET_DATETIME);
    }

    /**
     * Extracts and returns the numeric build number from the build text.
     * <p>
     * The method matches the build string against a predefined pattern to identify a numeric
     * sequence representing the build number. If a valid numeric sequence is found, it is parsed
     * and returned as a long value. If no numeric sequence is found, the method returns 0.
     */
    public final long buildNumber() {
        Matcher matcher = Revision.BUILDNUMBER.matcher(build());
        return matcher.find() ? Long.parseLong(matcher.group(1)) : 0;
    }

    /**
     * Retrieves a string representation of the release version with a default format of "0.0".
     */
    public final String release() {
        return toString("0.0");
    }

    /**
     * Retrieves a string representation of the version in the format "0.0.0".
     */
    public final String version() {
        return toString("0.0.0");
    }

    /**
     * Builds a new {@code Revision} instance using the current state and the provided
     * {@code Version}.
     */
    public final Revision build(Version version) {
        return new Revision(hash(), time(), version);
    }
}
