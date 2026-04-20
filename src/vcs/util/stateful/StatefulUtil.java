package vcs.util.stateful;

import vcs.core.model.VCS;

/**
 * The base class for all stateful utils.
 */
class StatefulUtil {
    /**
     * The current repository.
     */
    protected final VCS vcs;

    /**
     * Stores the current repository.
     * @param vcs the current repository object.
     */
    protected StatefulUtil(VCS vcs) {
        this.vcs = vcs;
    }
}
