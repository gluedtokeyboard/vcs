package io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.service.NodeService;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.service.RepoService;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Contains repository context methods.
 */
@Component
@RequiredArgsConstructor
public class RepoContext {
    private final IdStorage storage;
    private final VcsStore vcsStore;
    private final NodeService nodeService;
    private final RepoService repoService;

    /**
     * Searches for a repository upwards from the given directory.
     *
     * @param directoryId the ID of the starting directory for the search.
     * @return the repository context if a repository is found
     * @throws java.util.NoSuchElementException if a repository could not be found
     */
    public VcsContext getContextByDirectoryId(UUID directoryId) {
        String directory = nodeService.getPath(directoryId);
        return getContextByDirectory(directory);
    }

    /**
     * Returns the context for the given repository.
     *
     * @param repoId the repository ID
     * @return the context for the given repository
     * @throws java.util.NoSuchElementException if a repository with the given ID does not exist
     */
    public VcsContext getContextByRepoId(UUID repoId) {
        String directory = repoService.getRepoRoot(repoId);
        return getContextByDirectory(directory);
    }

    /**
     * Searches for a repository upwards from the given directory.
     *
     * @param directory the starting directory for the search.
     * @return the repository context if a repository is found
     * @throws java.util.NoSuchElementException if a repository could not be found
     */
    public VcsContext getContextByDirectory(String directory) {
        return VcsContext.getContext(storage, vcsStore, directory);
    }

    /**
     * Stores the {@link io.github.gluedtokeyboard.vcs.core.model.Vcs} object held by the given context at the
     * repository root.
     *
     * @param ctx the repository context
     */
    public void storeVcs(VcsContext ctx) {
        vcsStore.store(ctx.repoRoot(), ctx.vcs());
    }
}
