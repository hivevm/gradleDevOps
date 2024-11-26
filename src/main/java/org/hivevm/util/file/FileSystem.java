// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

/**
 * The {@link FileSystem} class.
 */
public abstract class FileSystem {

  /**
   * Constructs an instance of {@link FileSystem}.
   */
  private FileSystem() {}

  /**
   * Delete the {@link File} or the whole directory and all its files.
   *
   * @param file
   */
  public static boolean delete(File file) {
    if (file.isDirectory()) {
      if (!file.exists()) {
        return false;
      }

      try {
        Files.walk(file.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        return true;
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return file.delete();
  }

  /**
   * Get an absolute {@link File}.
   *
   * @param path
   */
  public static File getFile(String path, File workingDir) {
    return FileSystem.getFile(new File(path), workingDir);
  }

  /**
   * Get an absolute {@link File}.
   *
   * @param file
   */
  public static File getFile(File file, File workingDir) {
    return file.isAbsolute() ? file : workingDir.toPath().resolve(file.toPath()).toFile();
  }
}
