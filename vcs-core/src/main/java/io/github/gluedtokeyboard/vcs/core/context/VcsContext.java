package io.github.gluedtokeyboard.vcs.core.context;

import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.NoSuchElementException;

/**
 * Provides the context for the repository.
 *
 * @param vcs      The repository object.
 * @param repoRoot The root of the repository.
 */
public record VcsContext(Vcs vcs, String repoRoot) {
    /**
     * Locates the repository root upwards from the given directory and returns a {@link VcsContext} object for it.
     *
     * @param storage   a {@link Storage} implementation.
     * @param directory the starting directory.
     * @return a {@link VcsContext} object for the found repository.
     */
    public static VcsContext getContext(Storage storage, VcsStore vcsStore, String directory) {
        String repoRoot = locateRepoRoot(storage, vcsStore, directory);
        Vcs vcs = vcsStore.load(repoRoot);
        return new VcsContext(vcs, repoRoot);
    }

    /**
     * Locates the repository root upwards from the given directory.
     *
     * @param storage   a {@link Storage} implementation.
     * @param directory the starting directory.
     * @return the repository root.
     * @throws NoSuchElementException if a repository could not be found.
     */
    private static String locateRepoRoot(Storage storage, VcsStore vcsStore, String directory) {
        while (!vcsStore.exists(directory)) {
            directory = storage.getDirectory(directory);
            if (directory == null) {
                throw new NoSuchElementException(
                        "A repository could not be found in the current directory or in any of its ancestors."
                );
            }
        }
        return directory;
    }
}
