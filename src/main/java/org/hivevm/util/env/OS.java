// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.env;


/**
 * The {@link OS} defines the available operating systems.
 */
public enum OS {

  MACOS,
  LINUX,
  WINDOWS;

  private static OS instance = null;

  /**
   * Return <code>true</code> if it is windows.
   */
  public static boolean isMacOS() {
    return OS.current() == OS.MACOS;
  }

  /**
   * Return <code>true</code> if it is windows.
   */
  public static boolean isLinux() {
    return OS.current() == OS.LINUX;
  }

  /**
   * Return <code>true</code> if it is windows.
   */
  public static boolean isWindows() {
    return OS.current() == OS.WINDOWS;
  }

  /**
   * Get the current operating system.
   */
  public static OS current() {
    if (OS.instance == null) {
      String name = System.getProperty("os.name").toLowerCase();
      if (name.contains("windows")) {
        OS.instance = OS.WINDOWS;
      } else if (name.contains("mac")) {
        OS.instance = OS.MACOS;
      } else {
        OS.instance = OS.LINUX;
      }
    }
    return OS.instance;
  }
}
