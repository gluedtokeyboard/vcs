package io.github.gluedtokeyboard.vcs.api.vcs.repo.service;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.Init;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import org.springframework.stereotype.Service;

/**
 * {@link Init} with ID support.
 */
@Service
public class IdInit extends Init {
    private final RepoService repoService;

    /**
     * Stores the given interface implementations.
     *
     * @param storage     an {@link IdStorage}
     * @param vcsLayout   a {@link VcsLayout} implementation
     * @param vcsStore    a {@link VcsStore} implementation
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation
     * @param repoService the object containing service methods for
     *                    {@link io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo}
     */
    public IdInit(
            IdStorage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, RepoService repoService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.repoService = repoService;
    }

    @Override
    public void init(String repoRoot) {
        super.init(repoRoot);
        repoService.createRepo(repoRoot);
    }
}
