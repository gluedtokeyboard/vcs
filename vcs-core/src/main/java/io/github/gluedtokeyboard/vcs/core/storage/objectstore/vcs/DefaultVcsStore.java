package io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs;

import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.persistence.objectstore.ObjectStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * A {@link VcsStore} implementation using {@link ObjectStore}, {@link VcsLayout}, and {@link Storage}.
 */
public class DefaultVcsStore implements VcsStore {
    /**
     * The loading and storing of {@link Vcs} objects are handled by this object.
     */
    private final ObjectStore objectStore;

    /**
     * Contains the layout of the repository internals.
     */
    private final VcsLayout vcsLayout;

    /**
     * The object used to interact with the storage.
     */
    private final Storage storage;

    /**
     * Initializes the store with the given objects.
     * @param objectStore the object used for loading and storing {@link Vcs} objects.
     * @param vcsLayout the object containing the layout of the repository internals.
     * @param storage a {@link Storage} implementation.
     */
    public DefaultVcsStore(ObjectStore objectStore, VcsLayout vcsLayout, Storage storage) {
        this.objectStore = objectStore;
        this.vcsLayout = vcsLayout;
        this.storage = storage;
    }

    /**
     * Stores the given {@link Vcs} object.
     *
     * @param repoRoot the repository root.
     * @param vcs      the {@link Vcs} object to store.
     */
    @Override
    public void store(String repoRoot, Vcs vcs) {
        String location = vcsLayout.getObjectLocation(repoRoot);
        objectStore.store(vcs, location);
    }

    /**
     * Loads the {@link Vcs} object of the given repository.
     *
     * @param repoRoot the repository root.
     * @return the {@link Vcs} object of the given repository.
     */
    @Override
    public Vcs load(String repoRoot) {
        String location = vcsLayout.getObjectLocation(repoRoot);
        return objectStore.load(location, Vcs.class);
    }

    /**
     * Checks if a {@link Vcs} object exists.
     *
     * @param repoRoot the repository root.
     * @return a boolean indicating whether a {@link Vcs} object exists.
     */
    @Override
    public boolean exists(String repoRoot) {
        String location = vcsLayout.getObjectLocation(repoRoot);
        return storage.exists(location);
    }

    /**
     * Deletes the {@link Vcs} object of the given repository.
     *
     * @param repoRoot the repository root.
     */
    @Override
    public void delete(String repoRoot) {
        String location = vcsLayout.getObjectLocation(repoRoot);
        storage.rm(location);
    }
}
