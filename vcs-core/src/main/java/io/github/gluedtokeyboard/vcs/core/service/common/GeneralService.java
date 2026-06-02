package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.function.Consumer;

/**
 * General repository methods.
 */
public class GeneralService extends BaseService {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public GeneralService(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Walks the repository root.
     * @param repoRoot the repository root.
     * @param consumeFiles indicates whether to pass files to the given {@link Consumer}.
     * @param consumeDirsPreVisit indicates whether to pass directories to the given {@link Consumer} before visiting
     *                           them.
     * @param consumeDirsPostVisit indicates whether to pass directories to the given {@link Consumer} after visiting
     *                            them.
     * @param consumer a {@link Consumer} to consume the repository files and directories.
     */
    public void walkRepo(
            String repoRoot, boolean consumeFiles, boolean consumeDirsPreVisit, boolean consumeDirsPostVisit,
            Consumer<String> consumer) {
        String repoFolder = vcsLayout.getRepoFolder(repoRoot);
        storage.walk(
                repoRoot, path -> path.equals(repoFolder), consumeFiles, consumeDirsPreVisit,
                consumeDirsPostVisit, consumer);
    }

    /**
     * Walks the repository root, but only feeds files to the consumer.
     * @param repoRoot the repository root.
     * @param consumer a {@link Consumer} to consume the repository files.
     */
    public void walkRepoFiles(String repoRoot, Consumer<String> consumer) {
        walkRepo(repoRoot, true, false, false, consumer);
    }

    /**
     * Walks the repository root, but only feeds directories to the consumer before visiting them.
     * @param repoRoot the repository root.
     * @param consumer a {@link Consumer} to consume all the directories in the repository.
     */
    public void walkRepoDirsPre(String repoRoot, Consumer<String> consumer) {
        walkRepo(repoRoot, false, true, false, consumer);
    }

    /**
     * Walks the repository root, but only feeds directories to the consumer after visiting them.
     * @param repoRoot the repository root.
     * @param consumer a {@link Consumer} to consume all the directories in the repository.
     */
    public void walkRepoDirsPost(String repoRoot, Consumer<String> consumer) {
        walkRepo(repoRoot, false, false, true, consumer);
    }

    /**
     * Relativizes the given path with respect to the repository root.
     * @param repoRoot the repository root.
     * @param path an absolute path.
     * @return the given path relative to the repository root.
     */
    public String getRepoRelativePath(String repoRoot, String path) {
        return storage.getRelativePath(repoRoot, path);
    }

    /**
     * Resolves the given repository-relative path against the repository root.
     * @param repoRoot the repository root.
     * @param repoRelativePath the path to resolve against the repository root.
     * @return the given path resolved against the repository root.
     */
    public String getAbsolutePath(String repoRoot, String repoRelativePath) {
        return storage.join(repoRoot, repoRelativePath);
    }
}
