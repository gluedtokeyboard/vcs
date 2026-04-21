package vcs.core.service.command;

import vcs.core.model.VCS;

import java.nio.file.Path;
import java.util.List;

import static vcs.util.FileSystem.exists;
import static vcs.util.FileSystem.mkdir;

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
        if (repoExists()) {
            throw new RuntimeException("A repository already exists in the current working directory.");
        }
        createRepo();
    }

    /**
     * Creates a repository in the current working directory.
     */
    public void createRepo() {
        createRepoFolders();
        vcs.serialize();
    }

    /**
     * Creates the folders required to store the data and the metadata of the repository.
     */
    private void createRepoFolders() {
        List<Path> folders = List.of(
                VCS.getStagingAreaFolder(),
                VCS.getCommitsFolder(),
                VCS.getBlobsFolder()
        );
        for (Path folder : folders) {
            mkdir(folder);
        }
    }

    /**
     * Checks if a repository exists in the current working directory.
     * @return {@code true} if a repository exists; {@code false} otherwise.
     */
    public boolean repoExists() {
        return exists(VCS.getRepoFolder());
    }
}
