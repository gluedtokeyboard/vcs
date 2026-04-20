package vcs.command;

import vcs.core.model.VCS;
import vcs.util.stateful.CommitUtil;
import vcs.util.stateful.General;
import vcs.util.stateful.Restoration;
import vcs.util.stateful.StagingArea;

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
    protected final StagingArea stagingAreaUtil;

    /**
     * Utils for working with commits.
     */
    protected final CommitUtil commitUtil;

    /**
     * General repository utils.
     */
    protected final General generalUtil;

    /**
     * File and commit restoration utils.
     */
    protected final Restoration restorationUtil;

    /**
     * Stores the current repository and initializes the repository util objects with it.
     * @param vcs the current repository object.
     */
    protected Command(VCS vcs) {
        this.vcs = vcs;
        this.stagingAreaUtil = new StagingArea(vcs);
        this.commitUtil = new CommitUtil(vcs);
        this.generalUtil = new General(vcs);
        this.restorationUtil = new Restoration(vcs);
    }
}
