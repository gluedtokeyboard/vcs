package vcs.commands;

import vcs.core.VCS;
import vcs.utils.stateful.CommitUtils;
import vcs.utils.stateful.General;
import vcs.utils.stateful.StagingArea;

/**
 * The base class for all commands.
 */
class Command {
    /**
     * The current repository.
     */
    protected final VCS vcs;

    /**
     * Staging area utils.
     */
    protected final StagingArea stagingAreaUtils;

    /**
     * Utils for working with commits.
     */
    protected final CommitUtils commitUtils;

    /**
     * General repository utils.
     */
    protected final General generalUtils;

    /**
     * Stores the current repository and initializes the repository util objects with it.
     * @param vcs the current repository object.
     */
    protected Command(VCS vcs) {
        this.vcs = vcs;
        this.stagingAreaUtils = new StagingArea(vcs);
        this.commitUtils = new CommitUtils(vcs);
        this.generalUtils = new General(vcs);
    }
}
