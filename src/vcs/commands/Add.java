package vcs.commands;

import vcs.core.Blob;
import vcs.core.VCS;

import java.nio.file.Path;

import static vcs.utils.FileSystem.*;
import static vcs.utils.Serialization.serialize;

/**
 * Methods for staging a file.
 */
public class Add extends Command {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public Add(VCS vcs) {
        super(vcs);
    }

    /**
     * Stages the given file.
     * @param path the path of the file.
     */
    public void add(String path) {
        Path p = joinCWD(path);
        if (!exists(p)) {
            throw new RuntimeException("File does not exist");
        }
        if (vcs.isStaged(p)) {
            unstageFile(p);
        }
        stageFile(p);
    }

    /**
     * Stages the given file.
     * @param path the path of the file.
     */
    private void stageFile(Path path) {
        Blob blob = new Blob(path);
        vcs.stageFile(path, blob.getSha1());
        moveBlobToStagingArea(blob);
    }

    /**
     * Unstages the given file.
     * @param path the path of the file.
     */
    private void unstageFile(Path path) {
        removeBlobFromStagingArea(path);
        vcs.unstageFile(path);
    }

    /**
     * Removes the given file's blob from the staging area.
     * @param path The path of the file.
     */
    private void removeBlobFromStagingArea(Path path) {
        String sha1 = vcs.getStagedSha(path);
        Path location = getBlobDestination(sha1);
        rm(location);
    }

    /**
     * Serializes the blob in the staging area folder.
     * @param blob the blob to serialize.
     */
    private static void moveBlobToStagingArea(Blob blob) {
        Path destination = getBlobDestination(blob.getSha1());
        serialize(blob, destination);
    }

    /**
     * Returns the blob's destination in the staging area.
     * @param sha1 The SHA-1 hash of the blob.
     * @return The blob's destination in the staging area.
     */
    private static Path getBlobDestination(String sha1) {
        return join(VCS.getStagingAreaFolder(), sha1);
    }
}
