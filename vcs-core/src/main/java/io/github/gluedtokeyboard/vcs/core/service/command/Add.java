package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;

import java.nio.file.Path;
import java.util.Objects;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.exists;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.joinCWD;

/**
 * Methods for staging a file.
 */
public class Add extends BaseCommand {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public Add(Vcs vcs) {
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
            stagingAreaService.unstageFile(p);
        }
        if (hasChanged(p)) {
            stagingAreaService.stageFile(p);
        }
    }

    /**
     * Checks if the given file has changed compared to the previous commit.
     * @param path the path of the file.
     * @return {@code true} if the file has changed; {@code false} otherwise.
     */
    private boolean hasChanged(String path) {
        Commit commit = commitService.getLatestCommitCurrentBranch();
        if (commit == null) {
            return true;
        }
        String previousHash = commit.getFileHash(path);
        Blob blob = new Blob(path);
        return !Objects.equals(blob.getSha1(), previousHash);
    }
}
