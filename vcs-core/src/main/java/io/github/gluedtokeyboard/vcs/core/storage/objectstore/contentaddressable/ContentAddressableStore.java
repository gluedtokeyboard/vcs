package io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable;

/**
 * The store for content-addressable objects.
 * @param <T> the type of the content-addressable object.
 */
public interface ContentAddressableStore<T> {
    /**
     * Stores the given content-addressable object.
     * @param repoRoot the repository root.
     * @param object the content-addressable object to store.
     */
    void store(String repoRoot, T object);

    /**
     * Loads the object matching the given hash.
     * @param repoRoot the repository root.
     * @param hash the hash of the object.
     * @return the object matching the given hash.
     */
    T load(String repoRoot, String hash);

    /**
     * Checks if an object with the given hash exists.
     * @param repoRoot the repository root.
     * @param hash the hash of the object.
     * @return a boolean indicating whether an object with the given hash exists.
     */
    boolean exists(String repoRoot, String hash);

    /**
     * Deletes the object with the given hash.
     * @param repoRoot the repository root.
     * @param hash the hash of the object.
     */
    void delete(String repoRoot, String hash);
}
