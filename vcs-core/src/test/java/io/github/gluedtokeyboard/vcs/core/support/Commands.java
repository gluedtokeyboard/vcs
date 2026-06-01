package io.github.gluedtokeyboard.vcs.core.support;

import io.github.gluedtokeyboard.vcs.core.service.command.*;
import io.github.gluedtokeyboard.vcs.core.service.common.*;
import io.github.gluedtokeyboard.vcs.core.support.infra.VcsInfra;

/**
 * Lazily loads all the VCS commands.
 */
public final class Commands {
    public final VcsInfra vcsInfra;

    private GeneralService generalService;
    private StagingAreaService stagingAreaService;
    private CommitService commitService;
    private WorkingTreeService workingTreeService;

    private Init init;
    private Add add;
    private CommitCommand commitCommand;
    private Rm rm;
    private Log log;
    private Status status;
    private Branch branch;
    private Checkout checkout;
    private Reset reset;
    private Merge merge;

    /**
     * Stores the infrastructure required for the commands and the services.
     * @param vcsInfra the infrastructure for the commands and the services.
     */
    public Commands(VcsInfra vcsInfra) {
        this.vcsInfra = vcsInfra;
    }

    private GeneralService generalService() {
        if (generalService == null) {
            generalService = new GeneralService(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore);
        }
        return generalService;
    }

    private StagingAreaService stagingAreaService() {
        if (stagingAreaService == null) {
            stagingAreaService = new StagingAreaService(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    generalService());
        }
        return stagingAreaService;
    }

    private CommitService commitService() {
        if (commitService == null) {
            commitService = new CommitService(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    generalService());
        }
        return commitService;
    }

    private WorkingTreeService workingTreeService() {
        if (workingTreeService == null) {
            workingTreeService = new WorkingTreeService(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    status(), generalService(), commitService());
        }
        return workingTreeService;
    }

    public Init init() {
        if (init == null) {
            init = new Init(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore);
        }
        return init;
    }

    public Add add() {
        if (add == null) {
            add = new Add(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    generalService(), stagingAreaService(), commitService());
        }
        return add;
    }

    public CommitCommand commitCommand() {
        if (commitCommand == null) {
            commitCommand = new CommitCommand(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    commitService(), stagingAreaService());
        }
        return commitCommand;
    }

    public Rm rm() {
        if (rm == null) {
            rm = new Rm(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    generalService(), stagingAreaService());
        }
        return rm;
    }

    public Log log() {
        if (log == null) {
            log = new Log(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore);
        }
        return log;
    }

    public Status status() {
        if (status == null) {
            status = new Status(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    generalService(), stagingAreaService(), commitService());
        }
        return status;
    }

    public Branch branch() {
        if (branch == null) {
            branch = new Branch(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore);
        }
        return branch;
    }

    public Checkout checkout() {
        if (checkout == null) {
            checkout = new Checkout(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    generalService(), workingTreeService());
        }
        return checkout;
    }

    public Reset reset() {
        if (reset == null) {
            reset = new Reset(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    workingTreeService());
        }
        return reset;
    }

    public Merge merge() {
        if (merge == null) {
            merge = new Merge(
                    vcsInfra.storage, vcsInfra.vcsLayout, vcsInfra.vcsStore, vcsInfra.blobStore, vcsInfra.commitStore,
                    add(), rm(), commitCommand(), generalService(), commitService(), workingTreeService());
        }
        return merge;
    }
}
