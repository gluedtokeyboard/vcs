package io.github.gluedtokeyboard.vcs.core.support.infra.storage;

import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * {@link Storage} utilities.
 */
public final class StorageUtils {
    /**
     * Modifies the given file so that its contents differ from the previous version.
     *
     * @param storage the {@link Storage} object containing the file
     * @param path the path of the file
     */
    public static void modifyFile(final Storage storage, final String path) {
        final String originalContents = storage.readString(path);
        final String newContents = originalContents + ".";
        storage.write(newContents, path);
    }
}
