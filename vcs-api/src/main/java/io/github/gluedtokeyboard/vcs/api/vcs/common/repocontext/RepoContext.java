package io.github.gluedtokeyboard.vcs.api.vcs.common.repocontext;

import io.github.gluedtokeyboard.vcs.api.filesystem.common.infrastructure.IdStorage;
import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Contains repository context methods.
 */
@Component
@RequiredArgsConstructor
public class RepoContext {
    private final IdStorage storage;
    private final VcsStore vcsStore;

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
}
