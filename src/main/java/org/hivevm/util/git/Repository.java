// Copyright 2024 HiveVM.ORG. All rights reserved.
// SPDX-License-Identifier: MIT

package org.hivevm.util.git;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.MergeCommand.FastForwardMode;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.SubmoduleConfig.FetchRecurseSubmodulesMode;
import org.eclipse.jgit.merge.ContentMergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.submodule.SubmoduleWalk;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.TagOpt;
import org.hivevm.util.Version;

/**
 * Represents a repository that encapsulates functionalities for managing Git repositories,
 * including operations such as fetching, pulling, pushing, committing, and stashing changes. This
 * class also facilitates access to repository metadata and management of operations across multiple
 * submodules.
 *
 * @see https://github.com/centic9/jgit-cookbook.
 */
public class Repository implements AutoCloseable {

    private static final Pattern HASH = Pattern.compile("^[0-9a-fA-F]{40,40}$");


    private final Git                 git;
    private final AnyObjectId         oid;
    private final CredentialsProvider credentials;
    private final List<Throwable>     exceptions;

    /**
     * Constructs an instance of {@link Repository}.
     */
    Repository(Git git, CredentialsProvider credentials) {
        this.git = git;
        this.oid = null;
        this.credentials = credentials;
        this.exceptions = new ArrayList<>();
    }

    /**
     * Constructs an instance of {@link Repository}.
     */
    Repository(Git git, AnyObjectId oid, Repository parent) {
        this.git = git;
        this.oid = oid;
        this.credentials = parent.credentials;
        this.exceptions = parent.exceptions;
    }

    /**
     * Retrieves the working directory of the repository.
     */
    public final File getLocation() {
        return getGit().getRepository().getWorkTree();
    }

    /**
     * Retrieves the {@link Git} instance associated with the repository.
     */
    protected final Git getGit() {
        return this.git;
    }

    /**
     * Retrieves the object ID associated with this repository.
     */
    protected final AnyObjectId getObjectId() {
        return this.oid;
    }

    /**
     * Retrieves the {@link CredentialsProvider} associated with the repository.
     */
    protected final CredentialsProvider getCredentials() {
        return this.credentials;
    }

    /**
     * Retrieves the list of exceptions recorded in the repository. The method clears the internal
     * collection of exceptions after retrieving them, effectively transferring the ownership of the
     * exceptions to the caller.
     */
    public final List<Throwable> getExceptions() {
        var errors = new ArrayList<>(this.exceptions);
        this.exceptions.clear();
        return errors;
    }

    /**
     * Catches and aggregates exceptions from the repository and throws them as a single exception
     * if any are present, otherwise returns the current {@link Repository} instance.
     */
    public final Repository catchAndThrow() {
        var list = getExceptions();
        if (!list.isEmpty())
            throw new RuntimeException(
                list.stream().map(t -> t.getMessage()).collect(Collectors.joining("\n")));
        return this;
    }

    /**
     * Handles exceptions by adding them to the internal collection of recorded exceptions.
     */
    protected final void handleException(Exception exception) {
        this.exceptions.add(exception);
    }

    /**
     * Retrieves a {@link RepositoryVerbose} instance associated with the current
     * {@link Repository}. The {@link RepositoryVerbose} provides additional verbose operations for
     * interacting with the Git repository.
     */
    protected final RepositoryVerbose getVerbose() {
        return new RepositoryVerbose(this.git, this.credentials);
    }

    /**
     * Retrieves the name of the current branch in the Git repository.
     */
    public final String getBranch() throws IOException {
        return getGit().getRepository().getBranch();
    }

    /**
     * Retrieves the {@link Revision} associated with the specified {@link Version} from the
     * repository.
     */
    public final Revision getRevision(Version version) throws IOException {
        return RepositoryVersion.getRevision(getGit(), version);
    }

    /**
     * Performs a fetch operation on the Git repository and its submodules.
     * <p>
     * This method retrieves new changes from the remote repository and updates the local repository
     * accordingly. It also recursively fetches changes for all submodules. The following options
     * are configured for the fetch operation: - Credentials from the associated
     * `CredentialsProvider` are used for authentication. - Tags from the remote repository are
     * fetched. - Deleted references in the remote repository are removed locally. - Fetched objects
     * are verified for integrity. - Submodules are recursively fetched.
     * <p>
     * If any exceptions occur during the fetch process, they are handled and recorded using the
     * repository's exception handling mechanism.
     */
    public final void fetch() {
        forEach(r -> r.fetch());

        var command = getGit().fetch();
        command.setCredentialsProvider(getCredentials());
        command.setTagOpt(TagOpt.FETCH_TAGS);
        command.setRemoveDeletedRefs(true);
        command.setCheckFetchedObjects(true);
        command.setRecurseSubmodules(FetchRecurseSubmodulesMode.YES);

        try {
            command.call();
        } catch (GitAPIException e) {
            handleException(e);
        }
    }

    /**
     * Pulls all changes from the remote repository to the local repository and updates submodules.
     * <p>
     * This method performs a pull operation on the associated Git repository and its submodules.
     * The following configurations are applied during the pull operation: - Authentication is
     * provided using the associated {@link CredentialsProvider}. - The fast-forward mode is set to
     * {@link PullCommand.FastForwardMode#FF_ONLY}. - The content merge strategy is set to
     * {@link ContentMergeStrategy#OURS}. - Fetches changes recursively for all submodules using
     * {@link FetchRecurseSubmodulesMode#YES}.
     * <p>
     * If the pull operation is not successful, an instance of {@link RepositoryException} is
     * thrown. Any exceptions during the process are handled through the repository's exception
     * handling mechanism.
     */
    public final void pull() {
        forEach(r -> r.pull());

        var command = getGit().pull();
        command.setCredentialsProvider(getCredentials());
        command.setFastForward(FastForwardMode.FF_ONLY);
        command.setContentMergeStrategy(ContentMergeStrategy.OURS);
        command.setRecurseSubmodules(FetchRecurseSubmodulesMode.YES);

        try {
            PullResult result = command.call();
            if (!result.isSuccessful()) {
                handleException(new RepositoryException("Pull aborted"));
            }
        } catch (GitAPIException e) {
            handleException(e);
        }
    }

    /**
     * Pushes local changes to the remote repository and its submodules.
     * <p>
     * This method performs a push operation for the current repository and iterates through all its
     * submodules to push their respective changes. The following configurations are applied during
     * the push operation:
     * <p>
     * - Authentication is provided using the associated {@link CredentialsProvider}. - The push
     * operation is forced using {@code setForce(true)} to override remote changes. - Tags from the
     * local repository are pushed to the remote.
     * <p>
     * If any exceptions occur during the push operation, they are handled and recorded using the
     * repository's exception handling mechanism.
     * <p>
     * This operation ensures that both local commits and tags are synchronized with the remote
     * repository.
     */
    public final void push() {
        forEach(r -> r.push());

        var command = getGit().push();
        command.setCredentialsProvider(getCredentials());
        command.setForce(true);

        try {
            command.call();
        } catch (GitAPIException e) {
            handleException(e);
        }

        command = getGit().push();
        command.setCredentialsProvider(getCredentials());
        command.setForce(true);
        command.setPushTags();

        try {
            command.call();
        } catch (GitAPIException e) {
            handleException(e);
        }
    }

    /**
     * Commits changes to the repository with the specified commit message.
     */
    public final void commit(String message) {
        forEach(r -> r.commit(message));

        var command = getGit().commit();
        command.setCredentialsProvider(getCredentials());
        command.setMessage(message);
        command.setAll(true);

        try {
            if (!getGit().status().call().isClean())
                command.call();
        } catch (GitAPIException e) {
            handleException(e);
        }
    }

    /**
     * Executes the checkout process for each repository in the collection.
     * <p>
     * This method iterates through a collection of repositories, performing the following actions
     * for each repository: 1. Attempts to stash changes in the repository by calling the `stash`
     * method. 2. Checks out a specific commit in the repository by retrieving the commit associated
     * with its object ID and performing the checkout operation.
     * <p>
     * In case of any exceptions during the stash or checkout operations (e.g., GitAPIException or
     * IOException), the exception is handled by invoking the `handleException` method.
     * <p>
     * This method is designed for scenarios where the state of multiple repositories needs to be
     * preserved and reset to a particular commit across the collection.
     * <p>
     * Note: Proper error handling is implemented to address potential issues that may arise during
     * the stash or checkout operations to ensure stability during execution.
     */
    public final void checkout() {
        forEach(r -> {
            try {
                r.stash(null);
                r.checkout(r.getCommit(r.getObjectId()));
            } catch (GitAPIException | IOException e) {
                handleException(e);
            }
        });
    }

    /**
     * Performs a hard reset and checkout operation for the repository.
     * <p>
     * This method retrieves the current branch using {@link #getBranch()} and attempts to perform a
     * hard checkout by invoking the {@link #checkout(String)} method with the branch name. If any
     * exceptions such as {@link GitAPIException} or {@link IOException} occur during the operation,
     * they are handled by invoking the {@link #handleException(Exception)} method.
     * <p>
     * The primary purpose of this method is to reset and clean the repository's working directory
     * to match the latest state of the specified branch.
     */
    public final void checkoutHard() {
        try {
            checkout(getBranch());
        } catch (GitAPIException | IOException e) {
            handleException(e);
        }
    }

    /**
     * Checks out the specified branch or creates a new branch if it does not exist. This method
     * also iterates over all submodules of the repository, stashing their changes (if any) and
     * checking out the corresponding branch within each submodule.
     * <p>
     * If the branch does not exist in the repository, it is created and set to track the remote
     * branch with the same name.
     */
    public final RevCommit checkout(String name) throws GitAPIException, IOException {
        var ref = getGit().getRepository().findRef(name);

        var command = getGit().checkout();
        command.setCreateBranch(ref == null);
        command.setStartPoint("origin/" + name).setName(name);
        ref = command.call();
        forEach(r -> {
            try {
                r.stash(null);
                r.checkout(r.getCommit(r.getObjectId()), name);
            } catch (GitAPIException | IOException e) {
                handleException(e);
            }
        });
        return ref == null ? null : getCommit(ref.getObjectId());
    }

    /**
     * Creates a Git tag with the specified name in the repository.
     * <p>
     * This method performs the following actions: - Initializes a tag creation command using the
     * associated Git instance. - Sets the tag name to the provided {@code tagName}. - Forces the
     * update of the tag, if it already exists. - Executes the tag creation command.
     * <p>
     * If an exception occurs during the process, it is handled and recorded using the repository's
     * exception handling mechanism.
     */
    public final void tag(String tagName) {
        var command = getGit().tag();
        command.setName(tagName);
        command.setForceUpdate(true);

        try {
            command.call();
        } catch (GitAPIException e) {
            handleException(e);
        }
    }

    /**
     * Creates a new stash in the Git repository with the provided message.
     * <p>
     * This method utilizes the Git stash command to save the current working directory state.
     * Untracked files are not included in the stash. The provided message is used as the index and
     * working directory message for the stash.
     * <p>
     * If an exception occurs during the stash operation, it is handled and recorded using the
     * repository's exception handling mechanism.
     */
    public final void stash(String message) {
        var command = getGit().stashCreate();
        command.setIncludeUntracked(false);
        command.setIndexMessage(message);
        command.setWorkingDirectoryMessage(message);

        try {
            command.call();
        } catch (GitAPIException e) {
            handleException(e);
        }
    }

    /**
     * Retrieves a specific commit from the repository corresponding to the given name.
     * <p>
     * The method checks if the provided name matches an object ID hash. If it does, it retrieves
     * the commit associated with that object ID. Otherwise, it attempts to find a Git reference by
     * the given name and retrieves the commit associated with that reference. If no such reference
     * is found, the method returns null.
     */
    public final RevCommit getCommit(String name) throws GitAPIException, IOException {
        var matcher = Repository.HASH.matcher(name);
        if (matcher.find())
            return getCommit(ObjectId.fromString(name));
        var ref = getGit().getRepository().findRef(name);
        return (ref == null) ? null : getCommit(ref.getObjectId());
    }

    /**
     * Retrieves a specific commit from the repository using its object ID.
     */
    protected final RevCommit getCommit(AnyObjectId id) throws GitAPIException, IOException {
        try (var walk = new RevWalk(getGit().getRepository())) {
            return walk.parseCommit(id);
        }
    }

    /**
     * Checks out a specified commit within the Git repository by performing branch operations. The
     * method retrieves the current branch from the repository, checks out the specified commit, and
     * updates the branch accordingly. If any errors occur during the process, they are handled
     * using the exception handling mechanism of the repository.
     */
    protected final void checkout(RevCommit commit) {
        try {
            checkout(commit, getGit().getRepository().getBranch());
        } catch (GitAPIException | IOException e) {
            handleException(e);
        }
    }

    /**
     * Checks out a specified commit to a branch in the Git repository by renaming, deleting,
     * recreating, and resetting the branch to the provided commit.
     */
    protected final void checkout(RevCommit commit, String branch) throws GitAPIException {
        var hash = commit.getId().getName();

        getGit().branchRename().setNewName(hash).setOldName(branch).call();
        getGit().branchDelete().setBranchNames(branch).setForce(true).call();
        getGit().checkout().setCreateBranch(true).setName(branch).setStartPoint(commit).call();
        getGit().branchDelete().setBranchNames(hash).setForce(true).call();
    }

    /**
     * Creates a new branch in the Git repository from the specified commit and switches to it.
     * <p>
     * This method initializes a branch creation command, sets its parameters such as branch name,
     * force creation, and starting point (provided commit), and executes the command. After
     * creating the branch, it checks out the specified commit to the newly created branch.
     */
    protected final void branch(RevCommit commit, String branch)
        throws GitAPIException, IOException {
        var command = getGit().branchCreate();
        command.setName(branch);
        command.setForce(true);
        command.setStartPoint(commit);
        command.call();
        checkout(commit, branch);
    }

    /**
     * Iterates over all submodules of the repository and performs the specified action on each of
     * them.
     * <p>
     * This method uses {@link SubmoduleWalk} to traverse the submodules of the current repository.
     * For each submodule, a new {@link Repository} instance is constructed and passed to the
     * provided {@link Consumer} action. The submodule's resources are properly closed after the
     * consumer action is executed. If an exception occurs during processing, it is handled using
     * the repository's exception handling mechanism.
     */
    public final void forEach(Consumer<Repository> consumer) {
        try (var walk = SubmoduleWalk.forIndex(getGit().getRepository())) {
            while (walk.next()) {
                if (walk.getRepository() != null) {
                    var git = new Git(walk.getRepository());
                    try (var repo = new Repository(git, walk.getObjectId(), this)) {
                        consumer.accept(repo);
                    }
                }
            }
        } catch (IOException e) {
            handleException(e);
        }
    }

    /**
     * Releases resources used by the {@link Repository} instance.
     * <p>
     * This method performs the following actions: - Iterates through the collection of exceptions
     * and prints stack traces for each one. - Closes the associated Git repository to release any
     * resources it holds.
     * <p>
     * It is recommended to call this method when the repository is no longer needed to ensure
     * proper cleanup of resources.
     */
    @Override
    public final void close() {
        this.exceptions.forEach(Throwable::printStackTrace);
        this.git.getRepository().close();
    }
}
