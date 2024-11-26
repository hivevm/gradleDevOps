// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.ConfigInvalidException;
import org.eclipse.jgit.lib.ProgressMonitor;
import org.eclipse.jgit.lib.TextProgressMonitor;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.FilterSpec;
import org.eclipse.jgit.transport.TagOpt;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.hivevm.util.Builder;


/**
 * Provides a builder for constructing and managing a GIT repository. The RepositoryBuilder supports
 * operations such as configuring the remote URL, setting the branch, managing credentials, adding
 * submodules, and enabling a progress monitor. It facilitates fetching or cloning a repository if
 * it does not already exist locally and ensures the proper setup of configured submodules.
 */
public class RepositoryBuilder implements Builder<Repository> {

    private final File location;


    private String remote;
    private String branch;
    private String username;
    private String password;
    private boolean isBare;
    private FilterSpec filterSpec;

    private       ProgressMonitor monitor;
    private final Set<String>     modules = new LinkedHashSet<>();

    /**
     * Constructs a new RepositoryBuilder with the specified repository location.
     *
     */
    public RepositoryBuilder(File location) {
        this.location = location;
    }

    /**
     * Sets the remote URL for the repository.
     */
    public final RepositoryBuilder setRemote(String remote) {
        this.remote = remote;
        return this;
    }

    /**
     * Sets the branch for the repository.
     */
    public final RepositoryBuilder setBranch(String branch) {
        this.branch = branch;
        return this;
    }

    /**
     * Sets the credentials for accessing the repository.
     */
    public final RepositoryBuilder setCredentials(String username, String password) {
        this.username = username;
        this.password = password;
        return this;
    }

    /**
     * Configures the repository to be bare or non-bare.
     */
    public final RepositoryBuilder setBare(boolean isBare) {
        this.isBare = isBare;
        return this;
    }

    /**
     * Sets the filter specification for the repository.
     */
    public final RepositoryBuilder setFilterSpec(FilterSpec filterSpec) {
        this.filterSpec = filterSpec;
        return this;
    }

    /**
     * Adds the specified submodules to the repository configuration.
     */
    public final RepositoryBuilder addSubModules(String... modules) {
        this.modules.addAll(Arrays.asList(modules));
        return this;
    }

    /**
     * Enables monitoring functionality for the repository operations by utilizing a text-based
     * progress monitor.
     */
    public final RepositoryBuilder enableMonitor() {
        this.monitor = new TextProgressMonitor();
        return this;
    }

    /**
     * Checks whether the repository at the specified location is available.
     */
    public final boolean isAvailable() {
        return this.location.exists();
    }

    /**
     * Retrieves the credentials provider based on the configured username and password. If both the
     * username and password are defined, a {@link UsernamePasswordCredentialsProvider} is created
     * and returned. Otherwise, returns null.
     */
    protected final CredentialsProvider getCredentials() {
        if ((this.username != null) && (this.password != null)) {
            return new UsernamePasswordCredentialsProvider(this.username, this.password);
        }
        return null;
    }

    /**
     * Creates a {@link CloneCommand} configured with the specified location, remote URI, and
     * credentials. The command is set to fetch tags, does not clone submodules, and uses a progress
     * monitor.
     *
     * @param location    the target directory where the repository will be cloned
     * @param remote      the URI of the remote repository to clone
     * @param credentials the credentials provider for authenticating with the remote repository
     * @return the configured {@link CloneCommand} instance
     */
    private CloneCommand createClone(File location, String remote,
        CredentialsProvider credentials) {
        var command = Git.cloneRepository();
        command.setDirectory(location);
        command.setURI(remote).setCredentialsProvider(credentials);
        command.setTagOption(TagOpt.FETCH_TAGS);
        command.setCloneSubmodules(false);
        command.setProgressMonitor(this.monitor);
        return command;
    }

    /**
     * Retrieves a {@link Git} repository instance. If the repository exists at the specified
     * location, it initializes and returns the repository. If the repository does not exist, it
     * clones the repository from the remote URL using the provided credentials and returns it.
     */
    private Git getRepository(CredentialsProvider credentials) throws GitAPIException, IOException {
        if (this.location.exists()) {
            var builder = new FileRepositoryBuilder();
            builder.findGitDir(this.location);
            return new Git(builder.build());
        }

        if (this.remote == null)
            throw new IllegalArgumentException("Remote is required for a checkout");

        var command = createClone(this.location, this.remote, credentials)
            .setBare(isBare)
            .setBranch(this.branch);
        if (this.filterSpec != null)
            command.setTransportConfigCallback(t -> t.setFilterSpec(this.filterSpec));
        return command.call();
    }

    /**
     * Builds and returns a {@link Repository} instance with the specified configuration. The method
     * initializes the main repository and its submodules based on the provided settings, including
     * credentials, branch, and modules. Submodules not explicitly included in the configuration are
     * skipped. The repository's monitoring and cloning behavior is defined by the configured
     * options within the builder.
     */
    @Override
    public final Repository build() {
        var credentials = getCredentials();
        try {
            var git = getRepository(credentials);
            var root = new Repository(git, credentials);
            try (var walk = SubmoduleWalk.forIndex(git.getRepository())) {
                while (walk.next()) {
                    if (!this.modules.contains(walk.getModulesPath()))
                        continue;

                    if (walk.getRepository() == null) {
                        var localPath = new File(git.getRepository().getWorkTree(), walk.getPath());
                        var command = createClone(localPath, walk.getRemoteUrl(), credentials);
                        try (var repo = new Repository(command.call(), walk.getObjectId(), root)) {
                            var rev = repo.getCommit(walk.getObjectId());
                            repo.branch(rev, this.branch);
                        }
                    }
                }
            } catch (ConfigInvalidException e) {
                throw new IllegalArgumentException(e);
            }
            return root;
        } catch (GitAPIException | IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
