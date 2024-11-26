// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * The {@link FileTreeCopying} copies a directory structure from source to the target path.
 */
public final class FileTreeCopying extends SimpleFileVisitor<Path> {

  private final Path source;
  private final Path target;


  private Instant instant;

  /**
   *
   * Constructs an instance of {@link FileTreeCopying}.
   *
   * @param source
   * @param target
   */
  private FileTreeCopying(Path source, Path target) {
    this.source = source;
    this.target = target;
  }

  /**
   * Resolves the path.
   *
   * @param path
   */
  private Path toPath(Path path) {
    return this.target.resolve(this.source.relativize(path));
  }

  /**
   * Resolves the path.
   *
   * @param path
   */
  private Instant toInstant(Path path) {
    try {
      BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
      return attr.creationTime().toInstant();
    } catch (Throwable ex) {}
    return null;
  }

  /**
   * Visit a directory.
   *
   * @param path
   * @param attrs
   */
  @Override
  public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
    Path dir = toPath(path);
    if (!Files.exists(dir)) {
      Files.createDirectory(dir);
    }
    return FileVisitResult.CONTINUE;
  }

  /**
   * Visit a file.
   *
   * @param path
   * @param attrs
   */
  @Override
  public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
    Path file = toPath(path);
    Files.copy(path, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES,
        LinkOption.NOFOLLOW_LINKS);

    Instant current = toInstant(path);
    if ((this.instant == null) || current.isAfter(this.instant)) {
      this.instant = current;
    }
    return FileVisitResult.CONTINUE;
  }


  /**
   * Copy the file tree using the environment variables.
   *
   * @param source
   * @param target
   */
  public static LocalDate copyFileTree(Path source, Path target) throws IOException {
    FileTreeCopying visitor = new FileTreeCopying(source, target);
    Files.walkFileTree(source, visitor);
    return visitor.instant == null ? LocalDate.now()
        : LocalDateTime.ofInstant(visitor.instant, ZoneOffset.UTC).toLocalDate();
  }
}