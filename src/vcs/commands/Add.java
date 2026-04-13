package vcs.commands;

import vcs.core.VCS;

import java.nio.file.Path;

import static vcs.utils.FileSystem.exists;
import static vcs.utils.FileSystem.joinCWD;

/**
 * Methods for staging a file.
 */
public class Add extends Command {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public Add(VCS vcs) {
        super(vcs);
    }

    /**
     * Stages the given file.
     * @param path the path of the file.
     */
    public void add(String path) {
        Path p = joinCWD(path);
        if (!exists(p)) {
            throw new RuntimeException("File does not exist");
        }
        if (vcs.isStaged(p)) {
            repoUtils.unstageFile(p);
        }
        repoUtils.stageFile(p);
    }
}
