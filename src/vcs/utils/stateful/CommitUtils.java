package vcs.utils.stateful;

import vcs.core.Commit;
import vcs.core.VCS;
import vcs.utils.stateless.FileSystem;

import java.nio.file.Path;

import static vcs.utils.stateless.FileSystem.join;
import static vcs.utils.stateless.FileSystem.ls;
import static vcs.utils.stateless.Serialization.deserialize;

/**
 * Commit utils.
 */
public final class CommitUtils extends StatefulUtil {
    /**
     * The representation of a null commit.
     */
    public static final String NULL_COMMIT = "";

    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public CommitUtils(VCS vcs) {
        super(vcs);
    }

    /**
     * Returns the destination for the blob when a commit is made.
     * @param sha1 the SHA-1 hash of the blob.
     * @return the blob's destination when a commit is made.
     */
    public static Path getBlobDestinationCommit(String sha1) {
        return join(VCS.getBlobsFolder(), sha1);
    }

    /**
     * Returns the latest commit in the current branch.
     * @return the latest commit in the current branch, or {@code null} if there are no commits.
     */
    public Commit getLatestCommitCurrentBranch() {
        String latestHash = getLatestHashCurrentBranch();
        if (latestHash == null) {
            return null;
        }
        return Commit.deserialize(latestHash);
    }

    /**
     * Returns the hash of the latest commit in the current branch.
     * @return the hash of the latest commit in the current branch, or {@code null} if there are no commits in the
     * current branch.
     */
    private String getLatestHashCurrentBranch() {
        return vcs.getCurrentBranchHead();
    }

    /**
     * Returns the commit matching the given commit ID.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @return the commit matching the given commit ID, or {@code null} if the commit isn't found.
     */
    public static Commit getCommit(String commitId) {
        Path directory = Commit.getDestinationFolder(commitId);
        Commit[] commit = {null};
        ls(directory).forEach(path -> {
            String basename = FileSystem.basename(path);
            if (basename.startsWith(commitId)) {
                commit[0] = deserialize(path, Commit.class);
            }
        });
        return commit[0];
    }
}
