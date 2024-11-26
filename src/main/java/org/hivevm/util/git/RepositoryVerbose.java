// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.CredentialsProvider;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Consumer;

/**
 * The {@link RepositoryVerbose} class.
 */
class RepositoryVerbose {

  private final Git                 repository;
  private final CredentialsProvider credentials;

  /**
   * Constructs an instance of {@link RepositoryVerbose}.
   *
   * @param repository
   * @param credentials
   */
  public RepositoryVerbose(Git repository, CredentialsProvider credentials) {
    this.repository = repository;
    this.credentials = credentials;
  }

  /**
   * List all branches.
   */
  public final Collection<Ref> listBranches() throws GitAPIException {
    return this.repository.branchList().setListMode(ListMode.ALL).call();
  }

  /**
   * List all local tags.
   */
  public final Collection<Ref> listTags() throws GitAPIException {
    return this.repository.tagList().call();
  }

  /**
   * List all remote tags.
   */
  public final Collection<Ref> listRemoteTags() throws GitAPIException {
    return this.repository.lsRemote().setCredentialsProvider(this.credentials).setTags(true).call();
  }

  /**
   * Iterates of every sub-module.
   *
   * @param module
   */
  public final void forEach(Consumer<SubmoduleWalk> module) throws IOException {
    try (SubmoduleWalk generator = SubmoduleWalk.forIndex(this.repository.getRepository())) {
      while (generator.next()) {
        module.accept(generator);
      }
    }
  }
}
