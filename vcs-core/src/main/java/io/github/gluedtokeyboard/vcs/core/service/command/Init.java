package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.List;

/**
 * Contains methods for initializing the repository.
 */
public class Init extends BaseCommand {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public Init(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Initializes a repository in the given location.
     * @param repoRoot the location for the new repository.
     * @throws RuntimeException if a repository already exists in the given location.
     */
    public void init(String repoRoot) {
        if (vcsStore.exists(repoRoot)) {
            throw new RuntimeException("A repository already exists in: " + repoRoot);
        }
        createRepo(repoRoot);
    }

    /**
     * Creates a repository in the given location.
     * @param repoRoot the location for the new repository.
     */
    public void createRepo(String repoRoot) {
        createRepoFolders(repoRoot);
        Vcs vcs = new Vcs();
        vcsStore.store(repoRoot, vcs);
    }

    /**
     * Creates the folders required to store the data and the metadata of the repository.
     * @param repoRoot the location of the new repository.
     */
    private void createRepoFolders(String repoRoot) {
        List<String> folders = List.of(
                vcsLayout.getCommitsFolder(repoRoot),
                vcsLayout.getBlobsFolder(repoRoot)
        );
        for (String folder : folders) {
            storage.mkdir(folder);
        }
    }
}
