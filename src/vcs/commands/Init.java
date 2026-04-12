package vcs.commands;

import vcs.core.VCS;

/**
 * Contains methods for initializing the repository.
 */
public class Init extends Command {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public Init(VCS vcs) {
        super(vcs);
    }

    /**
     * Initializes a repository in the current working directory.
     * @throws RuntimeException If a repository already exists in the current working directory.
     */
    public void init() {
        if (vcs.repoExists()) {
            throw new RuntimeException("A repository already exists in the current working directory.");
        }
        vcs.createRepo();
    }
}
