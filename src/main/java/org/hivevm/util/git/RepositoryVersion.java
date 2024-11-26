// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.RevWalkUtils;
import org.hivevm.util.Version;

/**
 * The {@link RepositoryVersion} class provides utility methods for managing and retrieving
 * versioning information from a Git repository. This includes extracting commit details, counting
 * commits, retrieving tags, and building version data.
 */
abstract class RepositoryVersion {

    private static final Pattern PATTERN = Pattern.compile(
        "(?<major>\\d+)[./](?<minor>\\d+)(?:[./](?<patch>\\d+))?(?:-(?<name>[a-zA-Z0-9.]+))?(?:\\+(?<build>[a-zA-Z0-9.]+))?");


    /**
     * The {@code RepositoryVersion} class serves as a utility class, providing static methods to
     * retrieve versioning information from a Git repository. It contains private constructors to
     * prevent instantiation and enforce its usage as a utility class.
     */
    private RepositoryVersion() {
    }

    /**
     * Converts the author time of the given Git commit into an {@link OffsetDateTime} object.
     */
    public static OffsetDateTime getTime(RevCommit revCommit) {
        var instant = revCommit.getAuthorIdent().getWhenAsInstant();
        return instant.atZone(ZoneId.systemDefault()).toOffsetDateTime();
    }

    /**
     * Calculates the total number of commits in the specified Git repository.
     */
    public static long getCommitCount(Git git) throws GitAPIException {
        return StreamSupport.stream(git.log().call().spliterator(), false).count();
    }

    /**
     * Retrieves the revision information from a Git repository, including the commit hash, commit
     * time, and version information.
     */
    public static Revision getRevision(Git git, Version version) throws IOException {
        var branch = git.getRepository().getBranch();
        var refId = git.getRepository().resolve("HEAD");

        try (var walk = new RevWalk(git.getRepository())) {
            var revCommit = walk.parseCommit(refId);
            var time = RepositoryVersion.getTime(revCommit);
            var hash = revCommit.getName().substring(0, 9);
            var buildNumber = RepositoryVersion.getCommitCount(git);

            if (Version.NONE.equals(version)) {
                var stream = RepositoryVersion.getTags(git, revCommit, walk).stream();
                version = stream.map(TagInfo::version).findFirst().orElse(Version.of(0, 0));
            }

            return new Revision(hash, time, version.build(buildNumber).preRelease(branch));
        } catch (GitAPIException e) {
            throw new IOException("Revision is not available on GIT", e);
        }
    }

    /**
     * Retrieves a collection of {@link TagInfo} objects representing the tags in a Git repository
     * that are merged into a specified commit.
     * <p>
     * This method filters and processes the tags of the repository, determining whether each tag is
     * merged into the given revision. If a tag is merged, it computes the commit count and extracts
     * version information for the tag. The results are returned as a sorted collection of
     * {@link TagInfo}.
     */
    private static Collection<TagInfo> getTags(Git git, RevCommit rev, RevWalk walk)
        throws GitAPIException {
        return git.tagList().call().stream().map(tag -> {
            try {
                var tagCommit = walk.parseCommit(tag.getObjectId());
                if (walk.isMergedInto(tagCommit, rev)) {
                    var version = Version.parse(tag.getName(), RepositoryVersion.PATTERN);
                    var count = RevWalkUtils.count(walk, rev, tagCommit);
                    return new TagInfo(tag, count, version);
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            return new TagInfo(tag, -1, null);
        }).filter(i -> i.count != -1).sorted().collect(Collectors.toList());
    }


    /**
     * Represents metadata about a Git tag, including its reference, associated commit count, and
     * version information. This class is primarily used to encapsulate data about tags in a
     * structured manner and provides comparison capabilities based on the commit count and
     * version.
     */
    public record TagInfo(Ref ref, int count, Version version)
        implements Comparable<TagInfo> {

        /**
         * Constructs a new {@link TagInfo} object with the specified reference. This constructor
         * allows for initializing a {@link TagInfo} instance with a Git reference, while other
         * properties are set to their default values.
         */
        private TagInfo(Ref ref) {
            this(ref, -1, null);
        }

        /**
         * Retrieves the name of the Git reference associated with this tag.
         */
        public String name() {
            return ref().getName();
        }

        @Override
        public int compareTo(TagInfo o) {
            return (this.count == o.count) ? this.version.compareTo(o.version)
                : Integer.compare(this.count, o.count);
        }
    }
}
