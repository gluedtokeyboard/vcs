package vcs.commands;

import vcs.core.VCS;

/**
 * The base class for all commands.
 */
class Command {
    /**
     * The current repository.
     */
    protected final VCS vcs;

    /**
     * Stores the current repository.
     * @param vcs the current repository object.
     */
    protected Command(VCS vcs) {
        this.vcs = vcs;
    }
}
