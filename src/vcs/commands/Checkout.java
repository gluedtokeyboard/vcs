package vcs.commands;

import vcs.core.Blob;
import vcs.core.Commit;
import vcs.core.VCS;
import vcs.utils.FileSystem;
import vcs.utils.Repo;

import java.nio.file.Path;

import static vcs.utils.Serialization.deserialize;
import static vcs.utils.FileSystem.joinCWD;
import static vcs.utils.FileSystem.ls;

/**
 * Methods for checking out files and branches.
 */
public class Checkout extends Command {
    /**
     * Stores the current repository, and initializes the repository utils object with it.
     *
     * @param vcs the current repository object.
     */
    public Checkout(VCS vcs) {
        super(vcs);
    }

    /**
     * Checks out the given file from the head of the current branch.
     * @param file the path of the file.
     * @throws RuntimeException if the given file isn't found in the head.
     */
    public void checkout(String file) {
        checkout(vcs.getCurrentBranchHead(), file);
    }

    /**
     * Checks out the given file from the given commit.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @param file the path of the file.
     * @throws RuntimeException if a commit with the given commit ID does not exist, or the given file isn't found in
     * the commit.
     */
    public void checkout(String commitId, String file) {
        Commit commit = getCommit(commitId);
        if (commit == null) {
            throw new RuntimeException("Commit with id " + commitId + " not found");
        }
        Path path = joinCWD(file);
        if (!commit.contains(path)) {
            throw new RuntimeException("File " + file + " not found in commit " + commit.getSha1());
        }
        restoreFile(commit, path);
    }

    /**
     * Restores the given file from the given commit.
     * @param commit the {@link Commit} object.
     * @param path the path of the file.
     */
    private void restoreFile(Commit commit, Path path) {
        String sha1 = commit.getFileHash(path);
        Path blobLocation = Repo.getBlobDestinationCommit(sha1);
        Blob blob = Blob.deserialize(blobLocation);
        blob.writeToFile(path);
    }

    /**
     * Returns the commit matching the given commit ID.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @return the commit matching the given commit ID, or {@code null} if the commit isn't found.
     */
    private Commit getCommit(String commitId) {
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
