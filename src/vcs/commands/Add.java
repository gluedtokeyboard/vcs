package vcs.commands;

import vcs.core.Blob;
import vcs.core.Commit;
import vcs.core.VCS;

import java.nio.file.Path;
import java.util.Objects;

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
        if (hasChanged(p)) {
            repoUtils.stageFile(p);
        }
    }

    /**
     * Checks if the given file has changed compared to the previous commit.
     * @param path the path of the file.
     * @return {@code true} if the file has changed; {@code false} otherwise.
     */
    private boolean hasChanged(Path path) {
        Commit commit = repoUtils.getLatestCommitCurrentBranch();
        if (commit == null) {
            return true;
        }
        String previousHash = commit.getFileHash(path);
        Blob blob = new Blob(path);
        return !Objects.equals(blob.getSha1(), previousHash);
    }
}
