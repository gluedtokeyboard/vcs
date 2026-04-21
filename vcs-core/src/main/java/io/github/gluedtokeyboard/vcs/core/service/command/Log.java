package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.VCS;

import java.util.ArrayList;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.NULL_COMMIT;

/**
 * Methods for obtaining the repository logs.
 */
public class Log extends BaseCommand {
    /**
     * Stores the current repository, and initializes the repository utils object with it.
     *
     * @param vcs the current repository object.
     */
    public Log(VCS vcs) {
        super(vcs);
    }

    /**
     * Returns commits starting from the head, following their first parents.
     * @return An {@linkplain ArrayList} of commits starting from the head to the earliest reachable commit.
     */
    public ArrayList<Commit> log() {
        ArrayList<Commit> commits = new ArrayList<>();
        String currentHash = vcs.getCurrentBranchHead();
        while (!(currentHash == null || currentHash.equals(NULL_COMMIT))) {
            Commit commit = Commit.deserialize(currentHash);
            commits.addLast(commit);
            currentHash = commit.getParent1();
        }
        return commits;
    }
}
