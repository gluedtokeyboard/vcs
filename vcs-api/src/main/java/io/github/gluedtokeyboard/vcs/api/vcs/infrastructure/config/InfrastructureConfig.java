package io.github.gluedtokeyboard.vcs.api.vcs.infrastructure.config;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfrastructureConfig {
    @Bean
    public VcsLayout vcsLayout(IdStorage storage) {
        return new DefaultVcsLayout(storage);
    }

    @Bean
    public Serializer serializer() {
        return new JavaBinarySerializer();
    }

    @Bean
    public ObjectStore objectStore(IdStorage storage, Serializer serializer) {
        return new DefaultObjectStore(storage, serializer);
    }

    @Bean
    public VcsStore vcsStore(ObjectStore objectStore, VcsLayout vcsLayout, IdStorage storage) {
        return new DefaultVcsStore(objectStore, vcsLayout, storage);
    }

    @Bean
    public ContentAddressableStore<Blob> blobStore(VcsLayout vcsLayout, IdStorage storage) {
        return new BlobStore(vcsLayout, storage);
    }

    @Bean
    public ContentAddressableStore<Commit> commitStore(
            ObjectStore objectStore, VcsLayout vcsLayout, IdStorage storage) {
        return new CommitStore(objectStore, vcsLayout, storage);
    }
}
