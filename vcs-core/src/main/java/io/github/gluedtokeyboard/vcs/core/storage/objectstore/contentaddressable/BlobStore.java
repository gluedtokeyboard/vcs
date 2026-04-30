package io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * A store for {@link Blob} objects.
 */
public class BlobStore implements ContentAddressableStore<Blob> {
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
     * @param vcsLayout the object containing the layout of the repository internals.
     * @param storage a {@link Storage} implementation.
     */
    public BlobStore(VcsLayout vcsLayout, Storage storage) {
        this.vcsLayout = vcsLayout;
        this.storage = storage;
    }

    /**
     * Stores the given {@link Blob}.
     *
     * @param repoRoot the repository root.
     * @param blob     the {@link Blob} to store.
     */
    @Override
    public void store(String repoRoot, Blob blob) {
        String hash = blob.getSha1();
        String location = getBlobLocation(repoRoot, hash);
        storage.write(blob.getContents(), location);
    }

    /**
     * Loads the {@link Blob} matching the given hash.
     *
     * @param repoRoot the repository root.
     * @param hash     the hash of the {@link Blob}.
     * @return the {@link Blob} matching the given hash.
     */
    @Override
    public Blob load(String repoRoot, String hash) {
        String location = getBlobLocation(repoRoot, hash);
        return new Blob(storage, location);
    }

    /**
     * Checks if a {@link Blob} with the given hash exists.
     *
     * @param repoRoot the repository root.
     * @param hash     the hash of the {@link Blob}.
     * @return a boolean indicating whether a {@link Blob} with the given hash exists.
     */
    @Override
    public boolean exists(String repoRoot, String hash) {
        String location = getBlobLocation(repoRoot, hash);
        return storage.exists(location);
    }

    /**
     * Deletes the {@link Blob} with the given hash.
     *
     * @param repoRoot the repository root.
     * @param hash     the hash of the {@link Blob}.
     */
    @Override
    public void delete(String repoRoot, String hash) {
        String location = getBlobLocation(repoRoot, hash);
        storage.rm(location);
    }

    /**
     * Returns the location for the given {@link Blob}.
     * @param repoRoot the repository root.
     * @param hash the hash of the {@link Blob}.
     * @return the location for the given {@link Blob}.
     */
    private String getBlobLocation(String repoRoot, String hash) {
        String blobsFolder = vcsLayout.getBlobsFolder(repoRoot);
        return storage.join(blobsFolder, hash);
    }
}
