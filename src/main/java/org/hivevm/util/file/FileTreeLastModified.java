// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.file;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 * The {@link FileTreeLastModified} copies a directory structure from source to the target path.
 */
public final class FileTreeLastModified extends SimpleFileVisitor<Path> {

  private Instant instant;

  /**
   * Visit a directory.
   *
   * @param path
   * @param attrs
   */
  @Override
  public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) throws IOException {
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
    try {
      BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
      Instant current = attr.creationTime().toInstant();
      if ((this.instant == null) || current.isAfter(this.instant)) {
        this.instant = current;
      }
    } catch (Throwable ex) {
      throw new IOException(ex);
    }
    return FileVisitResult.CONTINUE;
  }


  /**
   * Copy the file tree using the environment variables.
   *
   * @param source
   */
  public static LocalDate lastModified(Path source) throws IOException {
    FileTreeLastModified visitor = new FileTreeLastModified();
    Files.walkFileTree(source, visitor);
    return visitor.instant == null ? LocalDate.now()
        : LocalDateTime.ofInstant(visitor.instant, ZoneOffset.UTC).toLocalDate();
  }
}