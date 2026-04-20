package vcs.command;

import vcs.core.Commit;
import vcs.core.VCS;

import java.nio.file.Path;

import static vcs.util.stateless.FileSystem.joinCWD;

/**
 * Methods for deleting a file.
 */
public class Rm extends Command {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public Rm(VCS vcs) {
        super(vcs);
    }

    /**
     * Stages the current file for removal and deletes it if it is tracked. If it isn't tracked, it is only removed
     * from the staging area if it is staged.
     * @param path the path of the file.
     */
    public void rm(String path) {
        Path p = joinCWD(path);
        boolean isStaged = vcs.isStaged(p);
        boolean isTracked = isTracked(p);
        if (isStaged) {
            stagingAreaUtil.unstageFile(p);
        }
        if (isTracked) {
            stagingAreaUtil.stageDeletion(p);
        }
        if (!(isStaged || isTracked)) {
            throw new RuntimeException("The given file is neither staged nor tracked.");
        }
    }

    /**
     * Checks if the given file is tracked.
     * @param path the path of the file
     * @return {@code true} if the file is tracked; {@code false} otherwise.
     */
    private boolean isTracked(Path path) {
        String parentHash = vcs.getCurrentBranchHead();
        Commit parentCommit = Commit.deserialize(parentHash);
        return parentCommit.contains(path);
    }
}
