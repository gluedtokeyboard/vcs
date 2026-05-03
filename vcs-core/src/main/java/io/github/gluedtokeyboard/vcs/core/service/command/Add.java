package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.nio.file.Path;
import java.util.Objects;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.exists;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.joinCWD;

/**
 * Methods for staging a file.
 */
public class Add extends BaseCommand {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public Add(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Stages the given file.
     * @param path the path of the file.
     */
    public void add(String path) {
        Path p = joinCWD(path);
        if (!exists(p)) {
            throw new RuntimeException("File does not exist");
        }
        if (vcs.isStaged(p)) {
            stagingAreaService.unstageFile(p);
        }
        if (hasChanged(p)) {
            stagingAreaService.stageFile(p);
        }
    }

    /**
     * Checks if the given file has changed compared to the previous commit.
     * @param path the path of the file.
     * @return {@code true} if the file has changed; {@code false} otherwise.
     */
    private boolean hasChanged(String path) {
        Commit commit = commitService.getLatestCommitCurrentBranch();
        if (commit == null) {
            return true;
        }
        String previousHash = commit.getFileHash(path);
        Blob blob = new Blob(path);
        return !Objects.equals(blob.getSha1(), previousHash);
    }
}
