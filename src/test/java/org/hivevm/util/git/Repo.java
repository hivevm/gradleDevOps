package org.hivevm.util.git;

import java.io.File;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

/**
 * The Git interface represents a utility for managing and interacting with a Git repository.
 * It defines constants for location, remote repository URL, branch, and user credentials,
 * supporting operations such as creating a repository builder pre-configured with these values.
 */
public record Repo(String remote, String branch, String username, String password) {

    public Repo(String remote, String branch) {
        this(remote, branch, null, null);
    }

    public Repo(String remote) {
        this(remote, "main", null, null);
    }

    public Repo(String remote, String username, String password) {
        this(remote, "main", username, password);
    }

    public CredentialsProvider credentials() {
        return username() == null || password() == null
            ? null
            : new UsernamePasswordCredentialsProvider(username(), password());
    }

    public static final File LOCATION = new File("/tmp/repo");
    public static final Repo LIB_GPKG = new Repo("https://github.com/hivevm/libgpkg2.git");

    /**
     * Creates and configures a new instance of {@link RepositoryBuilder} with default settings
     * such as location, remote URL, branch, credentials, and progress monitoring enabled.
     */
    public static RepositoryBuilder createBuilder() {
        var builder = new RepositoryBuilder(Repo.LOCATION);
        builder.setCredentials(Repo.LIB_GPKG.username(), Repo.LIB_GPKG.password());
        builder.setRemote(Repo.LIB_GPKG.remote()).setBranch(Repo.LIB_GPKG.branch());
        builder.enableMonitor();
        return builder;
    }
}
