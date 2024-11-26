// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import org.eclipse.jgit.api.errors.GitAPIException;


/**
 * The {@link RepositoryException} class.
 */
class RepositoryException extends GitAPIException {

  private static final long serialVersionUID = 1262732979855931184L;

  /**
   * Constructs an instance of {@link RepositoryException}.
   *
   * @param message
   */
  public RepositoryException(String message) {
    super(message);
  }
}
