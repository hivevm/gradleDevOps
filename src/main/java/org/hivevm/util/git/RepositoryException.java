// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import org.eclipse.jgit.api.errors.GitAPIException;


/**
 * Represents an exception that is specific to operations involving a repository.
 * <p>
 * This class is a subclass of {@link GitAPIException} and is used to indicate problems related to a
 * repository. It extends the GitAPIException to provide more specific error handling for
 * repository-related issues.
 */
class RepositoryException extends GitAPIException {

    private static final long serialVersionUID = 1262732979855931184L;

    /**
     * Constructs a new {@code RepositoryException} with the specified detail message.
     */
    public RepositoryException(String message) {
        super(message);
    }
}
