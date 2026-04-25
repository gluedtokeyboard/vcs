package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.GeneralService;
import io.github.gluedtokeyboard.vcs.core.service.common.RestorationService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;

/**
 * The base class for all commands.
 */
abstract class BaseCommand {
    /**
     * The current repository.
     */
    protected final Vcs vcs;

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
    protected BaseCommand(Vcs vcs) {
        this.vcs = vcs;
        this.stagingAreaService = new StagingAreaService(vcs);
        this.commitService = new CommitService(vcs);
        this.generalService = new GeneralService(vcs);
        this.restorationService = new RestorationService(vcs);
    }
}
