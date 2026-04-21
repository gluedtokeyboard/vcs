package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.VCS;

/**
 * The base class for all common service classes.
 */
abstract class BaseService {
    /**
     * The current repository.
     */
    protected final VCS vcs;

    /**
     * Stores the current repository.
     * @param vcs the current repository object.
     */
    protected BaseService(VCS vcs) {
        this.vcs = vcs;
    }
}
