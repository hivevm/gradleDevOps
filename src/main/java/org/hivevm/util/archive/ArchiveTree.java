// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.archive;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The {@link ArchiveTree} class.
 */
class ArchiveTree extends SimpleFileVisitor<Path> {

  private final Path       root;
  private final Pattern    pattern;

  private final List<File> files = new ArrayList<>();


  /**
   * Constructs an instance of {@link ArchiveTree}.
   *
   * @param root
   * @param pattern
   */
  private ArchiveTree(Path root, Pattern pattern) {
    this.root = root;
    this.pattern = pattern;
  }

  /**
   * Invoked for a directory before entries in the directory are visited.
   *
   * @param path
   * @param attrs
   */
  @Override
  public final FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
    checkPathPattern(path);
    return FileVisitResult.CONTINUE;
  }

  /**
   * Invoked for a file in a directory.
   *
   * @param path
   * @param attrs
   */
  @Override
  public final FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
    checkPathPattern(path);
    return FileVisitResult.SKIP_SUBTREE;
  }


  /**
   * Check against the pattern.
   *
   * @param path
   */
  private final void checkPathPattern(Path path) throws IOException {
    String input = this.root.relativize(path).toString();
    // Avoid problems on Windows
    Matcher matcher = this.pattern.matcher(input.replace('\\', '/'));
    if (matcher.find()) {
      this.files.add(new File(this.root.toFile(), input));
    }
  }

  /**
   * The {@link FileComparator} checks that the files are ordered in following order:
   *
   * <pre>
   * - directory
   * - regular file
   * - symbol link
   * </pre>
   */
  private static class FileComparator implements Comparator<File> {

    @Override
    public int compare(File o1, File o2) {
      int t1 = o1.isDirectory() ? 1 : ArchiveUtil.isSymbolicLink(o1) ? 3 : 2;
      int t2 = o2.isDirectory() ? 1 : ArchiveUtil.isSymbolicLink(o2) ? 3 : 2;
      return (t1 == t2) ? 0 : (t1 < t2 ? -1 : 1);
    }
  }

  /**
   * Copy the file tree using the environment variables.
   *
   * @param workingDir
   * @param pattern
   */
  public static List<File> findFiles(File workingDir, String filePattern) throws IOException {
    Path workingPath = workingDir.toPath();
    Pattern pattern = Pattern.compile("^" + filePattern.replace(".", "\\.").replace("*", "[^/]*"));
    ArchiveTree visitor = new ArchiveTree(workingPath, pattern);
    Files.walkFileTree(workingPath, visitor);
    Collections.sort(visitor.files, new FileComparator());
    return visitor.files;
  }
}
