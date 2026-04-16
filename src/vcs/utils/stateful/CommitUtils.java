package vcs.utils.stateful;

import vcs.core.Commit;
import vcs.core.VCS;

import java.nio.file.Path;

import static vcs.utils.stateless.FileSystem.join;

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
}
