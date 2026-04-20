package vcs.core.service.common;

import vcs.core.model.VCS;

/**
 * The base class for all common service classes.
 */
class Service {
    /**
     * The current repository.
     */
    protected final VCS vcs;

    /**
     * Stores the current repository.
     * @param vcs the current repository object.
     */
    protected Service(VCS vcs) {
        this.vcs = vcs;
    }
}
