// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.CredentialsProvider;

/**
 * The {@link RepositoryVerbose} class provides functionality to interact with a GIT repository in a
 * verbose manner. It includes methods for listing branches, tags, remote tags, and iterating over
 * submodules.
 */
class RepositoryVerbose {

    private final Git                 repository;
    private final CredentialsProvider credentials;

    /**
     * Constructs an instance of {@link RepositoryVerbose}.
     */
    public RepositoryVerbose(Git repository, CredentialsProvider credentials) {
        this.repository = repository;
        this.credentials = credentials;
    }

    /**
     * Lists all branches in the repository, including both local and remote branches.
     */
    public final Collection<Ref> listBranches() throws GitAPIException {
        return this.repository.branchList().setListMode(ListMode.ALL).call();
    }

    /**
     * Lists all tags in the repository.
     */
    public final Collection<Ref> listTags() throws GitAPIException {
        return this.repository.tagList().call();
    }

    /**
     * Lists all remote tags from the GIT repository using the provided credentials.
     */
    public final Collection<Ref> listRemoteTags() throws GitAPIException {
        return this.repository.lsRemote().setCredentialsProvider(this.credentials).setTags(true)
            .call();
    }

    /**
     * Iterates over all submodules of the repository and applies the specified action. The provided
     * {@link Consumer} is called for each submodule with a {@link SubmoduleWalk} instance, allowing
     * for custom processing of each submodule.
     */
    public final void forEach(Consumer<SubmoduleWalk> module) throws IOException {
        try (var generator = SubmoduleWalk.forIndex(this.repository.getRepository())) {
            while (generator.next()) {
                module.accept(generator);
            }
        }
    }
}
