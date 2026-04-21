package vcs.core.service.command;

import vcs.core.model.VCS;
import vcs.core.service.common.CommitService;
import vcs.core.service.common.GeneralService;
import vcs.core.service.common.RestorationService;
import vcs.core.service.common.StagingAreaService;

/**
 * The base class for all commands.
 */
class BaseCommand {
    /**
     * The current repository.
     */
    protected final VCS vcs;

    /**
     * Contains staging area methods.
     */
    protected final StagingAreaService stagingAreaService;

    /**
     * Contains methods for working with commits.
     */
    protected final CommitService commitService;

    /**
     * Contains general repository methods.
     */
    protected final GeneralService generalService;

    /**
     * Contains file and commit restoration methods.
     */
    protected final RestorationService restorationService;

    /**
     * Stores the current repository and initializes the common service objects with it.
     * @param vcs the current repository object.
     */
    protected BaseCommand(VCS vcs) {
        this.vcs = vcs;
        this.stagingAreaService = new StagingAreaService(vcs);
        this.commitService = new CommitService(vcs);
        this.generalService = new GeneralService(vcs);
        this.restorationService = new RestorationService(vcs);
    }
}
