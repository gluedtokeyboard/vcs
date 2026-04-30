package io.github.gluedtokeyboard.vcs.core.storage.layout;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * A {@link VcsLayout} implementation using {@link Storage}.
 */
public class DefaultVcsLayout implements VcsLayout {
    /**
     * The folder used to store the repository data and metadata.
     */
    private static final String REPO_FOLDER = ".vcs";

    /**
     * The name of the folder containing {@link Commit} objects.
     */
    private static final String COMMITS_FOLDER = "commits";

    /**
     * The name of the folder containing {@link Blob} objects of the committed files.
     */
    private static final String BLOBS_FOLDER = "blobs";

    /**
     * The name to use for storing the {@link Vcs} object.
     */
    private static final String VCS_OBJECT_NAME = "vcs";

    /**
     * The object used to interact with the storage.
     */
    private final Storage storage;

    /**
     * Stores the object used to interact with the storage.
     * @param storage a {@link Storage} implementation.
     */
    public DefaultVcsLayout(Storage storage) {
        this.storage = storage;
    }

    /**
     * Returns the repository's internal folder.
     *
     * @param repoRoot the repository root.
     * @return the absolute path of the repository's internal folder within {@link DefaultVcsLayout#storage}.
     */
    @Override
    public String getRepoFolder(String repoRoot) {
        return storage.join(repoRoot, REPO_FOLDER);
    }

    /**
     * Returns the blobs folder.
     *
     * @param repoRoot the repository root.
     * @return the absolute path of the blobs folder within {@link DefaultVcsLayout#storage}.
     */
    @Override
    public String getBlobsFolder(String repoRoot) {
        return resolveRepoFolder(repoRoot, BLOBS_FOLDER);
    }

    /**
     * Returns the commits folder.
     *
     * @param repoRoot the repository root.
     * @return the absolute path of the commits folder within {@link DefaultVcsLayout#storage}.
     */
    @Override
    public String getCommitsFolder(String repoRoot) {
        return resolveRepoFolder(repoRoot, COMMITS_FOLDER);
    }

    /**
     * Returns the location of the {@link Vcs} object.
     *
     * @param repoRoot the repository root.
     * @return the absolute path of the {@link Vcs} object within {@link DefaultVcsLayout#storage}.
     */
    @Override
    public String getObjectLocation(String repoRoot) {
        return resolveRepoFolder(repoRoot, VCS_OBJECT_NAME);
    }

    /**
     * Resolves the given path relative to the repository folder.
     * @param repoRoot the repository root.
     * @param path the relative path.
     * @return the absolute path obtained by resolving the given path against the repository folder within
     * {@link DefaultVcsLayout#storage}.
     */
    private String resolveRepoFolder(String repoRoot, String path) {
        String repoFolder = getRepoFolder(repoRoot);
        return storage.join(repoFolder, path);
    }
}
