package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.VCS;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;

import java.nio.file.Path;
import java.util.Date;
import java.util.TreeMap;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getBlobDestinationCommit;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.*;

/**
 * Methods for creating a commit.
 */
public class CommitCommand extends BaseCommand {
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
        commit(null, message);
    }

    /**
     * Creates a commit with the given second parent and message.
     * @param parent2 the SHA-1 hash of the second parent, or {@code null} if there is no second parent.
     * @param message the commit message.
     */
    void commit(String parent2, String message) {
        if (vcs.stagingAreaIsEmpty()) {
            throw new RuntimeException("No changes to commit");
        }
        if (message == null || message.isEmpty()) {
            throw new RuntimeException("Please enter a message");
        }
        Commit commit = createCommit(parent2, message);
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
     * Creates a new commit with the given second parent, message, and the staged files.
     * @param parent2 the SHA-1 hash of the second parent, or {@code null} if there is no second parent.
     * @param message the commit message.
     * @return the newly created commit.
     */
    private Commit createCommit(String parent2, String message) {
        Commit newCommit;
        Date timestamp = new Date();
        TreeMap<String, String> stagedFiles = vcs.getStagedFilesClone();
        Commit latestCommit = commitService.getLatestCommitCurrentBranch();
        if (latestCommit == null) {
            newCommit = new Commit(message, timestamp, stagedFiles);
        } else {
            TreeMap<String, String> files = latestCommit.getFilesClone();
            updateFiles(files, stagedFiles);
            if (parent2 == null) {
                newCommit = new Commit(message, timestamp, latestCommit.getSha1(), files);
            } else {
                newCommit = new Commit(message, timestamp, latestCommit.getSha1(), parent2, files);
            }
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
            if (value.equals(StagingAreaService.DELETION_MARKER)) {
                parentFiles.remove(key);
            } else {
                parentFiles.put(key, value);
            }
        });
    }
}
