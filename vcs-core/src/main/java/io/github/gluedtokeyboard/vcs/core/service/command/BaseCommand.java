package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * The base class for all commands.
 */
abstract class BaseCommand {
    /**
     * The object used to interact with the storage.
     */
    protected final Storage storage;

    /**
     * Contains the layout of the repository.
     */
    protected final VcsLayout vcsLayout;

    /**
     * A store for {@link Vcs} objects.
     */
    protected final VcsStore vcsStore;

    /**
     * A store for {@link Blob} objects.
     */
    protected final ContentAddressableStore<Blob> blobStore;

    /**
     * A store for {@link Commit} objects.
     */
    protected final ContentAddressableStore<Commit> commitStore;

    /**
     * Stores the given interface implementations.
     * @param storage a {@link Storage} implementation.
     * @param vcsLayout a {@link VcsLayout} implementation.
     * @param vcsStore a {@link VcsStore} implementation.
     * @param blobStore a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    protected BaseCommand(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        this.storage = storage;
        this.vcsLayout = vcsLayout;
        this.vcsStore = vcsStore;
        this.blobStore = blobStore;
        this.commitStore = commitStore;
    }
}
