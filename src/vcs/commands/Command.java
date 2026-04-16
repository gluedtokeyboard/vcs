package vcs.commands;

import vcs.core.VCS;
import vcs.utils.stateful.Repo;

/**
 * The base class for all commands.
 */
class Command {
    /**
     * The current repository.
     */
    protected final VCS vcs;

    /**
     * The object containing utility methods for the repository.
     */
    protected final Repo repoUtils;

    /**
     * Stores the current repository and initializes the repository utils object with it.
     * @param vcs the current repository object.
     */
    protected Command(VCS vcs) {
        this.vcs = vcs;
        this.repoUtils = new Repo(vcs);
    }
}
