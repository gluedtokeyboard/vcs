package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.VCS;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getCommit;

/**
 * Methods for resetting the current branch.
 */
public class Reset extends BaseCommand {
    /**
     * Stores the current repository, and initializes the repository utils object with it.
     *
     * @param vcs the current repository object.
     */
    public Reset(VCS vcs) {
        super(vcs);
    }

    /**
     * Resets the repository to the given commit.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     */
    public void reset(String commitId) {
        Commit commit = getCommit(commitId);
        if (commit == null) {
            throw new RuntimeException("Commit with id " + commitId + " not found");
        }
        restorationService.restoreCommitSafely(commit);
        vcs.setCurrentBranchHead(commit.getSha1());
    }
}
