package vcs.utils;

import vcs.core.VCS;

/**
 * Utils for the repository.
 */
public final class Repo {
    /**
     * The current repository.
     */
    private final VCS vcs;

    /**
     * Stores the current repository.
     * @param vcs the current repository object.
     */
    public Repo(VCS vcs) {
        this.vcs = vcs;
    }
}
