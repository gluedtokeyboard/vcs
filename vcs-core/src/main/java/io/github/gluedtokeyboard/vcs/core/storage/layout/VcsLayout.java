package io.github.gluedtokeyboard.vcs.core.storage.layout;

/**
 * Contains the layout of the repository internals.
 */
public interface VcsLayout {
    /**
     * Returns the repository's internal folder.
     * @param repoRoot the repository root.
     * @return the repository's internal folder.
     */
    String getRepoFolder(String repoRoot);

    /**
     * Returns the blobs folder.
     * @param repoRoot the repository root.
     * @return the blobs folder.
     */
    String getBlobsFolder(String repoRoot);

    /**
     * Returns the commits folder.
     * @param repoRoot the repository root.
     * @return the commits folder.
     */
    String getCommitsFolder(String repoRoot);

    /**
     * Returns the location of the {@link io.github.gluedtokeyboard.vcs.core.model.Vcs} object.
     * @param repoRoot the repository root.
     * @return the location of the {@link io.github.gluedtokeyboard.vcs.core.model.Vcs} object.
     */
    String getObjectLocation(String repoRoot);
}
