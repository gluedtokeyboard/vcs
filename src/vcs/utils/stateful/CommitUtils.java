package vcs.utils.stateful;

import vcs.commands.Status;
import vcs.core.Blob;
import vcs.core.Commit;
import vcs.core.VCS;
import vcs.utils.stateless.FileSystem;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import static vcs.utils.stateless.FileSystem.join;
import static vcs.utils.stateless.FileSystem.ls;
import static vcs.utils.stateless.General.setIntersection;
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

    /**
     * Checks if it is safe to apply the given commit.
     * @param commit the {@link Commit} to apply.
     * @throws RuntimeException if:
     * <ul>
     *     <li>The staging area isn't empty.</li>
     *     <li>There are unstaged modifications present.</li>
     *     <li>There are untracked files present that would be overwritten.</li>
     * </ul>
     */
    public void checkSafety(Commit commit) {
        if (!vcs.stagingAreaIsEmpty()) {
            throw new RuntimeException("The staging area isn't empty");
        }
        Status.StatusRecord statusRecord = new Status(vcs).status();
        if (hasUnstagedModifications(statusRecord)) {
            throw new RuntimeException("There are unstaged modifications present");
        }
        if (hasUntrackedConflicts(statusRecord, commit)) {
            throw new RuntimeException("There are untracked files present that would be overwritten");
        }
    }

    /**
     * Checks if the repository has unstaged modifications.
     * @param statusRecord a {@link vcs.commands.Status.StatusRecord} object containing the repository status.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private static boolean hasUnstagedModifications(Status.StatusRecord statusRecord) {
        return !(statusRecord.unstagedModifications().isEmpty() && statusRecord.unstagedDeletions().isEmpty());
    }

    /**
     * Checks if the repository has untracked files that would be overwritten while checking out the given
     * {@link Commit}.
     * @param statusRecord a {@link vcs.commands.Status.StatusRecord} object containing the repository status.
     * @param commit the {@link Commit} to check out.
     * @return a boolean indicating whether the repository has unstaged modifications.
     */
    private static boolean hasUntrackedConflicts(Status.StatusRecord statusRecord, Commit commit) {
        Set<String> commitFiles = commit.getCommitFiles().collect(Collectors.toSet());
        Set<String> untrackedFiles = statusRecord.untrackedFiles();
        return !setIntersection(commitFiles, untrackedFiles).isEmpty();
    }

    /**
     * Deserializes a committed {@link Blob}.
     * @param sha1 the SHA-1 hash of the {@link Blob}.
     * @return the deserialized {@link Blob}.
     */
    public static Blob getCommittedBlob(String sha1) {
        Path blobLocation = CommitUtils.getBlobDestinationCommit(sha1);
        return Blob.deserialize(blobLocation);
    }
}
