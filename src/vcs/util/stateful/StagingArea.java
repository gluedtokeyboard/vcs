package vcs.util.stateful;

import vcs.core.model.Blob;
import vcs.core.model.VCS;
import vcs.util.stateless.FileSystem;

import java.nio.file.Path;

import static vcs.util.stateless.FileSystem.join;
import static vcs.util.stateless.FileSystem.rm;

/**
 * Staging area utils.
 */
public final class StagingArea extends StatefulUtil {
    /**
     * A marker to indicate that a file is staged for deletion.
     */
    public static final String DELETION_MARKER = "";

    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public StagingArea(VCS vcs) {
        super(vcs);
    }

    /**
     * Stages the given file.
     * @param path the path of the file.
     */
    public void stageFile(Path path) {
        Blob blob = new Blob(path);
        vcs.stageFile(path, blob.getSha1());
        moveBlobToStagingArea(blob);
    }

    /**
     * Serializes the blob in the staging area folder.
     * @param blob the blob to serialize.
     */
    private static void moveBlobToStagingArea(Blob blob) {
        Path destination = getBlobDestinationStagingArea(blob.getSha1());
        blob.serialize(destination);
    }

    /**
     * Unstages the given file.
     * @param path the path of the file.
     */
    public void unstageFile(Path path) {
        removeBlobFromStagingArea(path);
        vcs.unstageFile(path);
    }

    /**
     * Removes the given file's blob from the staging area.
     * @param path The path of the file.
     */
    private void removeBlobFromStagingArea(Path path) {
        String sha1 = vcs.getStagedSha(path);
        Path location = getBlobDestinationStagingArea(sha1);
        rm(location);
    }

    /**
     * Stages the given file for deletion.
     * @param path the path of the file
     */
    public void stageDeletion(Path path) {
        FileSystem.rm(path);
        vcs.stageFile(path, DELETION_MARKER);
    }

    /**
     * Returns the blob's destination in the staging area.
     * @param sha1 The SHA-1 hash of the blob.
     * @return The blob's destination in the staging area.
     */
    private static Path getBlobDestinationStagingArea(String sha1) {
        return join(VCS.getStagingAreaFolder(), sha1);
    }
}
