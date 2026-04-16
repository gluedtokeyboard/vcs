package vcs.commands;

import vcs.core.Commit;
import vcs.core.VCS;
import vcs.utils.stateful.Repo;

import java.nio.file.Path;
import java.util.Date;
import java.util.TreeMap;

import static vcs.utils.stateless.FileSystem.*;
import static vcs.utils.stateful.Repo.getBlobDestinationCommit;

/**
 * Methods for creating a commit.
 */
public class CommitCommand extends Command {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public CommitCommand(VCS vcs) {
        super(vcs);
    }

    /**
     * Creates a commit with the given message.
     * @param message the commit message.
     */
    public void commit(String message) {
        if (vcs.stagingAreaIsEmpty()) {
            throw new RuntimeException("No changes to commit");
        }
        if (message == null || message.isEmpty()) {
            throw new RuntimeException("Please enter a message");
        }
        Commit commit = createCommit(message);
        vcs.setCurrentBranchHead(commit.getSha1());
        moveBlobsToBlobsFolder();
        vcs.clearStagingArea();
        commit.serialize();
    }

    /**
     * Moves the blobs from the staging area to the blobs folder.
     */
    private static void moveBlobsToBlobsFolder() {
        ls(VCS.getStagingAreaFolder()).forEach(
                path -> {
                    String sha1 = basename(path);
                    Path destination = getBlobDestinationCommit(sha1);
                    mv(path, destination);
                }
        );
    }

    /**
     * Creates a new commit with the given message and the staged files.
     * @param message the commit message.
     * @return the newly created commit.
     */
    private Commit createCommit(String message) {
        Commit newCommit;
        Date timestamp = new Date();
        TreeMap<String, String> stagedFiles = vcs.getStagedFilesClone();
        Commit latestCommit = repoUtils.getLatestCommitCurrentBranch();
        if (latestCommit == null) {
            newCommit = new Commit(message, timestamp, stagedFiles);
        } else {
            TreeMap<String, String> files = latestCommit.getFilesClone();
            updateFiles(files, stagedFiles);
            newCommit = new Commit(message, timestamp, latestCommit.getSha1(), files);
        }
        return newCommit;
    }

    /**
     * Updates the files of the parent commit in place with the staged files.
     * @param parentFiles the files of the parent commit.
     * @param stagedFiles the staged files.
     */
    private static void updateFiles(TreeMap<String, String> parentFiles, TreeMap<String, String> stagedFiles) {
        stagedFiles.forEach((key, value) -> {
            if (value.equals(Repo.DELETION_MARKER)) {
                parentFiles.remove(key);
            } else {
                parentFiles.put(key, value);
            }
        });
    }
}
