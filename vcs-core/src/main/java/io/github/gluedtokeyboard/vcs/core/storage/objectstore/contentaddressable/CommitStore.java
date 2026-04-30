package io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable;

import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.persistence.objectstore.ObjectStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.Optional;

/**
 * A store for {@link Commit} objects.
 */
public class CommitStore implements ContentAddressableStore<Commit> {
    /**
     * The loading and storing of {@link Commit} objects are handled by this object.
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
     * @param objectStore the object used for loading and storing {@link Commit} objects.
     * @param vcsLayout the object containing the layout of the repository internals.
     * @param storage a {@link Storage} implementation.
     */
    public CommitStore(ObjectStore objectStore, VcsLayout vcsLayout, Storage storage) {
        this.objectStore = objectStore;
        this.vcsLayout = vcsLayout;
        this.storage = storage;
    }

    /**
     * Stores the given {@link Commit}.
     *
     * @param repoRoot the repository root.
     * @param commit   the {@link Commit} to store.
     */
    @Override
    public void store(String repoRoot, Commit commit) {
        String hash = commit.getSha1();
        String location = getCommitLocation(repoRoot, hash);
        objectStore.store(commit, location);
    }

    /**
     * Loads the {@link Commit} matching the given commit prefix.
     *
     * @param repoRoot the repository root.
     * @param commitPrefix a prefix of the commit's hash.
     * @return the {@link Commit} matching the given hash.
     */
    @Override
    public Commit load(String repoRoot, String commitPrefix) {
        return findCommit(repoRoot, commitPrefix)
                .map(commit -> objectStore.load(commit, Commit.class))
                .orElse(null);
    }

    /**
     * Checks if a {@link Commit} with the given commit prefix exists
     *
     * @param repoRoot the repository root.
     * @param commitPrefix a prefix of the commit's hash.
     * @return a boolean indicating whether a {@link Commit} with the given commit prefix exists.
     */
    @Override
    public boolean exists(String repoRoot, String commitPrefix) {
        return findCommit(repoRoot, commitPrefix).isPresent();
    }

    /**
     * Deletes the {@link Commit} with the given hash.
     *
     * @param repoRoot the repository root.
     * @param hash     the hash of the {@link Commit}.
     */
    @Override
    public void delete(String repoRoot, String hash) {
        String location = getCommitLocation(repoRoot, hash);
        storage.rm(location);
    }

    /**
     * Returns the location for the given {@link Commit}.
     * @param repoRoot the repository root.
     * @param hash the hash of the {@link Commit}.
     * @return the location for the given {@link Commit}.
     */
    private String getCommitLocation(String repoRoot, String hash) {
        String destinationFolder = getDestinationFolder(repoRoot, hash);
        return storage.join(destinationFolder, hash);
    }

    /**
     * Returns the commit's destination folder. The destination folder is given by
     * {@code <commit folder>/<first two characters of the hash>}. This makes it easier to locate the commit when
     * searching by the prefixes of its hash.
     * @param repoRoot the repository root.
     * @param commitPrefix a prefix of the commit's hash.
     * @return the commit's destination folder.
     */
    private String getDestinationFolder(String repoRoot, String commitPrefix) {
        String commitsFolder = vcsLayout.getCommitsFolder(repoRoot);
        String subFolder = commitPrefix.substring(0, 2);
        return storage.join(commitsFolder, subFolder);
    }

    /**
     * Searches for a commit whose hash starts with the given prefix.
     * @param repoRoot the repository root.
     * @param commitPrefix a prefix of the commit's hash.
     * @return an {@link Optional} containing the absolute path of the commit if it is found.
     */
    private Optional<String> findCommit(String repoRoot, String commitPrefix) {
        String directory = getDestinationFolder(repoRoot, commitPrefix);
        if (!storage.exists(directory)) {
            return Optional.empty();
        }
        return storage.ls(directory).stream()
                .filter(commit -> storage.basename(commit).startsWith(commitPrefix))
                .findFirst();
    }
}
