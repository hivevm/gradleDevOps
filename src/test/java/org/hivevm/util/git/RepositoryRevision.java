
package org.hivevm.util.git;

import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.internal.storage.dfs.DfsRepositoryDescription;
import org.eclipse.jgit.internal.storage.dfs.InMemoryRepository;
import org.eclipse.jgit.transport.FilterSpec;
import org.eclipse.jgit.transport.RefSpec;
import org.hivevm.util.Version;
import org.junit.jupiter.api.Test;


public class RepositoryRevision {

    @Test
    public void testRevision() throws IOException {
        var builder = Repo.createBuilder();
        try (var repo = builder.build()) {
            var revision = repo.getRevision(Version.NONE);
            RepositoryTestUtil.printRevision(revision);

            repo.forEach(r -> {
                System.out.println(r.getGit().getRepository().getDirectory());
                try {
                    RepositoryTestUtil.printRevision(r.getRevision(revision));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    public static void main(String[] args) throws Exception {
        var noBlob = FilterSpec.fromFilterLine("blob:none");
        var noTree = FilterSpec.fromFilterLine("tree:0");
        var data = Repo.LIB_GPKG;

//        var builder = Repo.createBuilder();
//        builder.setBare(true).setFilterSpec(noTree);
//        try (var repo = builder.build()) {
//            System.out.println("");
//        }

//        var git = Git.cloneRepository()
//            .setDirectory(Repo.LOCATION)
//            .setURI(data.remote())
//            .setCredentialsProvider(data.credentials())
//            .setBare(true)
//            .setTransportConfigCallback(t -> t.setFilterSpec(noTree))
//            .call();

        var desc = new DfsRepositoryDescription("partial-memory-clone");
        var repo = new InMemoryRepository(desc);
        var git = new Git(repo);

        // Clone without blobs (commits and trees only)
        git.fetch()
            .setRemote(data.remote())
            .setCredentialsProvider(data.credentials())
            .setRefSpecs(
                new RefSpec("+refs/heads/*:refs/heads/*"),
                new RefSpec("+refs/tags/*:refs/tags/*")
            )
            .setTransportConfigCallback(transport -> transport.setFilterSpec(noTree))
            .call();

        System.out.printf("Branches:\t%s\n", git.branchList().call().size());
        System.out.printf("Tags:\t%s\n", git.tagList().call().size());
    }
}
