package vcs.core.service.common;

import vcs.core.model.VCS;

/**
 * The base class for all common service classes.
 */
class BaseService {
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
