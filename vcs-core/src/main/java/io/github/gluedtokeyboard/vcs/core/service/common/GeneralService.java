package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.Set;
import java.util.function.Consumer;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.getCWD;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.walk;

/**
 * General repository methods.
 */
public final class GeneralService extends BaseService {
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
     * @param consumeFiles indicates whether to pass files to the given {@link Consumer}.
     * @param consumeDirsPreVisit indicates whether to pass directories to the given {@link Consumer} before visiting
     *                           them.
     * @param consumeDirsPostVisit indicates whether to pass directories to the given {@link Consumer} after visiting
     *                            them.
     * @param consumer a {@link Consumer} to consume the repository files and directories.
     */
    public static void walkRepo(
            boolean consumeFiles, boolean consumeDirsPreVisit, boolean consumeDirsPostVisit,
            Consumer<String> consumer) {
        walk(getCWD(), Set.of(Vcs.getRepoFolder()), consumeFiles, consumeDirsPreVisit, consumeDirsPostVisit, consumer);
    }

    /**
     * Walks the repository root, but only feeds files to the consumer.
     * @param consumer a {@link Consumer} to consume the repository files.
     */
    public static void walkRepoFiles(Consumer<String> consumer) {
        walkRepo(true, false, false, consumer);
    }

    /**
     * Walks the repository root, but only feeds directories to the consumer before visiting them.
     * @param consumer a {@link Consumer} to consume all the directories in the repository.
     */
    public static void walkRepoDirsPre(Consumer<String> consumer) {
        walkRepo(false, true, false, consumer);
    }

    /**
     * Walks the repository root, but only feeds directories to the consumer after visiting them.
     * @param consumer a {@link Consumer} to consume all the directories in the repository.
     */
    public static void walkRepoDirsPost(Consumer<String> consumer) {
        walkRepo(false, false, true, consumer);
    }
}
