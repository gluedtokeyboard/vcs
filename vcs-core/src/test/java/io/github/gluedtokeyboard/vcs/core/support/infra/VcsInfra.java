package io.github.gluedtokeyboard.vcs.core.support.infra;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.DefaultVcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.BlobStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.CommitStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.DefaultVcsStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.persistence.objectstore.DefaultObjectStore;
import io.github.gluedtokeyboard.vcs.persistence.objectstore.ObjectStore;
import io.github.gluedtokeyboard.vcs.persistence.serialization.JavaBinarySerializer;
import io.github.gluedtokeyboard.vcs.persistence.serialization.Serializer;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.storage.inmemory.InMemoryStorage;

/**
 * Sets up the infrastructure required for the VCS.
 */
public final class VcsInfra {
    public final Storage storage;
    public final VcsLayout vcsLayout;
    public final VcsStore vcsStore;
    public final ContentAddressableStore<Blob> blobStore;
    public final ContentAddressableStore<Commit> commitStore;

    /**
     * Initializes infrastructure components with an {@linkplain InMemoryStorage}. The stores use a
     * {@link JavaBinarySerializer} for serialization.
     */
    public VcsInfra() {
        storage = new InMemoryStorage();
        vcsLayout = new DefaultVcsLayout(storage);
        Serializer serializer = new JavaBinarySerializer();
        ObjectStore objectStore = new DefaultObjectStore(storage, serializer);
        vcsStore = new DefaultVcsStore(objectStore, vcsLayout, storage);
        blobStore = new BlobStore(vcsLayout, storage);
        commitStore = new CommitStore(objectStore, vcsLayout, storage);
    }
}
