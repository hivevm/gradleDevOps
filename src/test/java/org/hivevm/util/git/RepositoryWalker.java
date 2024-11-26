
package org.hivevm.util.git;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.transport.FilterSpec;

/**
 * https://github.com/centic9/jgit-cookbook
 */
public class RepositoryWalker {

	private static final Pattern PATTERN_BACKLOG = Pattern.compile("(\\w+)\\s*-\\s*(\\d+)\\s*-[^\\n]+", Pattern.MULTILINE);
	private static final Pattern PATTERN_SVN = Pattern.compile("git-svn-id: svn://[^@]+@(\\d+)", Pattern.MULTILINE);

	public static Map<ObjectId, String> findAllBranches(Git git, String hash) throws IOException, GitAPIException {
		return git.nameRev()
			.addPrefix("refs/heads")
			.add(ObjectId.fromString(hash))
			.call();
	}

	public static List<RevCommit> findAllCommonAncestors(Repository repo, ObjectId commit1, ObjectId commit2) throws IOException, GitAPIException {
		List<RevCommit> ancestors = new ArrayList<>();
		try (RevWalk walk = new RevWalk(repo)) {
			RevCommit c1 = walk.parseCommit(commit1);
			RevCommit c2 = walk.parseCommit(commit2);

			walk.setRevFilter(RevFilter.MERGE_BASE);
			walk.markStart(c1);
			walk.markStart(c2);

			for (RevCommit commit : walk) {
				ancestors.add(commit);
			}
		}
		return ancestors;
	}

	private static Git getRepository() throws IOException, GitAPIException {
		var NO_BLOB = FilterSpec.fromFilterLine("blob:none");
		var NO_TREE = FilterSpec.fromFilterLine("tree:0");

//        var desc = new DfsRepositoryDescription("partial-memory-clone");
//        var repoLocation = new InMemoryRepository(desc);
//		var repoLocation = new FileRepository(LOCATION);

		// Clone without blobs (commits and trees only)
//        var git = new Git(repo)
//            .fetch()
//            .setRefSpecs(
//                new RefSpec("+refs/heads/*:refs/heads/*"),
//                new RefSpec("+refs/tags/*:refs/tags/*")
//            )
//            .setRemote(data.remote())
//            .setCredentialsProvider(data.credentials())
//            .setShallowSince(OffsetDateTime.now().minusYears(1))
//            .setTransportConfigCallback(t -> t.setFilterSpec(noTree))
//            .call();

//        var git = Git.cloneRepository()
//            .setDirectory(LOCATION)
//            .setBare(true)
//            .setURI(DATA.remote())
//            .setCredentialsProvider(DATA.credentials())
////            .setShallowSince(OffsetDateTime.now().minusYears(1))
//            .setTransportConfigCallback(t -> t.setFilterSpec(NO_TREE))
//            .call();

		return PDVersion.LOCATION.exists()
			? Git.open(PDVersion.LOCATION)
			: Git.cloneRepository()
				.setDirectory(PDVersion.LOCATION)
				.setBare(true)
				.setURI(PDVersion.REPO.remote())
				.setCredentialsProvider(PDVersion.REPO.credentials())
//            .setShallowSince(OffsetDateTime.now().minusYears(1))
				.setTransportConfigCallback(t -> t.setFilterSpec(NO_TREE))
				.call();
	}



	private static void printInfo(Git git) throws GitAPIException, IOException {
		System.out.printf("Branches:\t%s\n", git.branchList().call().size());
		System.out.printf("Branches:\t%s\n", git.branchList().call().stream().filter(r -> r.getName().startsWith("refs/heads/release/25")).count());
		System.out.printf("Tags:\t%s\n", git.tagList().call().size());

		findAllBranches(git,"f8d0b651ae3f104e37a8941af0c96dbdd4e9ce37")
			.values()
			.forEach(System.out::println);


		var head = "HEAD";
		var release_2509 = "refs/heads/release/2509";

		var repo = git.getRepository();


		var oidHead = repo.resolve(head);
		var oid2509 = repo.resolve(release_2509);
		var commit = findAllCommonAncestors(repo, oidHead, oid2509)
			.stream().findFirst();
		var anchestor = commit.map(c -> c.toObjectId().getName()).orElse(null);

		// a RevWalk allows to walk over commits based on some filtering that is defined
		try (var walk = new RevWalk(repo)) {
			walk.markStart(walk.parseCommit(oid2509));

			for (RevCommit rev : walk) {
				var matcher = PATTERN_SVN.matcher(rev.getFullMessage());
				if (matcher.find() && "143945".equals(matcher.group(1))) {
					System.out.println("Commit-Message: " + rev.getFullMessage());
					System.out.println("Commit-Commiter: " + rev.getCommitterIdent().getName());
					System.out.println("Commit-EMail: " + rev.getCommitterIdent().getEmailAddress());
					System.out.println("Commit-Time: " + Instant.ofEpochSecond(rev.getCommitTime()));
					System.out.println("Commit-Hash: " + rev.getId().getName());
					System.out.println("Commit-SVN: " + matcher.group(1));
				}
			}
			walk.dispose();
		}

		var svn = PDVersion.getVersions()
			.filter(v->v.version() == 25.09)
			.map(PDVersion::svnnumber)
			.collect(Collectors.toSet());


		// a RevWalk allows to walk over commits based on some filtering that is defined
		try (var walk = new RevWalk(repo)) {
			walk.markStart(walk.parseCommit(oid2509));
			var list = new ArrayList<String>();
			for (RevCommit rev : walk) {
				var matcher = PATTERN_BACKLOG.matcher(rev.getFullMessage());
				if (matcher.find())
					list.add(matcher.group(2));

				matcher = PATTERN_SVN.matcher(rev.getFullMessage());
				if (matcher.find() && svn.contains(Integer.valueOf(matcher.group(1)))) {
					System.out.printf("Commit-Hash/SVN: %s/%s\n", rev.getId().getName(), matcher.group(1));
				}

				if(rev.getId().getName().equals(anchestor)) {
					System.out.println("Found from, stopping walk");
					break;
				}
			}

			Collections.sort(list);
			System.out.println("Found " + list.size() + " commits: " + String.join(", ", list));
			System.out.println("Found " + new HashSet<>(list).size() + " commits");
			walk.dispose();
		}
	}

	private static RevCommit getAnchestorCommit(Repository repo, ObjectId head, ObjectId release) throws IOException, GitAPIException {
		return findAllCommonAncestors(repo, head, release)
			.stream().findFirst().orElse(null);
	}

	private static ObjectId getObjectId(Repository repository, String release) throws IOException, GitAPIException {
		return repository.resolve(String.format("refs/heads/release/%s", release));
	}

	private static Map<String, Backlog> getBacklogs(Repository repo, ObjectId branch, ObjectId anchestor) throws IOException, GitAPIException {
		var backlogs = new HashMap<String, Backlog>();

		// a RevWalk allows to walk over commits based on some filtering that is defined
		try (var walk = new RevWalk(repo)) {
			walk.markStart(walk.parseCommit(branch));

			for (RevCommit rev : walk) {
				var matcher = PATTERN_BACKLOG.matcher(rev.getFullMessage());
				if (matcher.find())
					backlogs.put(matcher.group(2), new Backlog(matcher.group(1), matcher.group(2)));
				else
					System.out.println("No backlog found: " + rev.getFirstMessageLine());

//				matcher = PATTERN_SVN.matcher(rev.getFullMessage());
//				if (matcher.find()) {
//					System.out.printf("Commit-Hash/SVN: %s/%s\n", rev.getId().getName(), matcher.group(1));
//				}

				if(rev.getId().getName().equals(anchestor.getName())) {
					System.out.printf("Found from, stopping walk %s\n", backlogs.size());
					return backlogs;
				}
			}
			walk.dispose();
		}
		return backlogs;
	}

	private static record Backlog(String product, String backlog){}

	public static void main(String[] args) throws Exception {
		try (var git = getRepository()) {
//			printInfo(git);

			var repo = git.getRepository();
			var head = repo.resolve("HEAD");
			var branch_09 = getObjectId(repo, "2509");
			var branch_10 = getObjectId(repo, "2510");

			var fork_09 = getAnchestorCommit(repo, head, branch_09);
			var fork_10 = getAnchestorCommit(repo, head, branch_10);

			System.out.printf("Commit-09: %s\n", fork_09.getId().toObjectId());
			System.out.printf("Commit-10: %s\n", fork_10.getId().toObjectId());

			System.out.println(String.join(",", getBacklogs(repo, branch_09, fork_09.getId().toObjectId()).keySet()));
			System.out.println(String.join(",", getBacklogs(repo, branch_10, fork_10.getId().toObjectId()).keySet()));


// 25.10RC4	=> 64bbd2f149e8a331f839a8f962cd8485b705127a
// 25.09m 	=> d1dae789cc28427ea6b4dc2f5c60542f8d90bfcf
			var rc = ObjectId.fromString("64bbd2f149e8a331f839a8f962cd8485b705127a");
			var m = ObjectId.fromString("d1dae789cc28427ea6b4dc2f5c60542f8d90bfcf");
			var fork = getAnchestorCommit(repo, rc, m);

			System.out.printf("Fork: %s\n", fork.getId().toObjectId());

			var map1 = getBacklogs(repo, rc, fork.getId().toObjectId());
			var map2 = getBacklogs(repo, m, fork.getId().toObjectId());

			System.out.println(String.join(",", map1.keySet()));
			System.out.println(String.join(",", map2.keySet()));

			var list = map1.entrySet().stream()
				.filter(e -> !map2.containsKey(e.getKey()))
				.filter(e -> !"HOTEL".equalsIgnoreCase(e.getValue().product()))
				.map(Map.Entry::getKey).toList();

			System.out.printf("(%s,%s) => %s, ",map1.size(), map2.size(), list.size());
			System.out.println(String.join(",", list.stream().sorted().collect(Collectors.toList())));
		}
	}
}
