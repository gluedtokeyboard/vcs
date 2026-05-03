package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.nio.file.Path;
import java.util.List;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.exists;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.mkdir;

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
     * Initializes a repository in the current working directory.
     * @throws RuntimeException If a repository already exists in the current working directory.
     */
    public void init() {
        if (repoExists()) {
            throw new RuntimeException("A repository already exists in the current working directory.");
        }
        createRepo();
    }

    /**
     * Creates a repository in the current working directory.
     */
    public void createRepo() {
        createRepoFolders();
        vcs.serialize();
    }

    /**
     * Creates the folders required to store the data and the metadata of the repository.
     */
    private void createRepoFolders() {
        List<Path> folders = List.of(
                Vcs.getStagingAreaFolder(),
                Vcs.getCommitsFolder(),
                Vcs.getBlobsFolder()
        );
        for (Path folder : folders) {
            mkdir(folder);
        }
    }

    /**
     * Checks if a repository exists in the current working directory.
     * @return {@code true} if a repository exists; {@code false} otherwise.
     */
    public boolean repoExists() {
        return exists(Vcs.getRepoFolder());
    }
}
