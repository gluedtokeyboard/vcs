package io.github.gluedtokeyboard.vcs.api.vcs.common.infrastructure.context;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import org.springframework.stereotype.Component;

/**
 * Aggregates infrastructure components for convenient usage.
 *
 * @param storage the object used to interact with the storage
 * @param vcsLayout an object containing the layout of the repository internals
 * @param vcsStore a store for {@link io.github.gluedtokeyboard.vcs.core.model.Vcs} objects
 * @param blobStore a store for {@link Blob} objects
 * @param commitStore a store for {@link Commit} objects
 */
@Component
public record InfrastructureContext(
        IdStorage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
        ContentAddressableStore<Commit> commitStore) {
}
