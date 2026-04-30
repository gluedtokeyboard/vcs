package io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs;

import io.github.gluedtokeyboard.vcs.core.model.Vcs;

/**
 * The store for {@link Vcs} objects.
 */
public interface VcsStore {
    /**
     * Stores the given {@link Vcs} object.
     * @param repoRoot the repository root.
     * @param vcs the {@link Vcs} object to store.
     */
    void store(String repoRoot, Vcs vcs);

    /**
     * Loads the {@link Vcs} object of the given repository.
     * @param repoRoot the repository root.
     * @return the {@link Vcs} object of the given repository.
     */
    Vcs load(String repoRoot);

    /**
     * Checks if a {@link Vcs} object exists.
     * @param repoRoot the repository root.
     * @return a boolean indicating whether a {@link Vcs} object exists.
     */
    boolean exists(String repoRoot);

    /**
     * Deletes the {@link Vcs} object of the given repository.
     * @param repoRoot the repository root.
     */
    void delete(String repoRoot);
}
