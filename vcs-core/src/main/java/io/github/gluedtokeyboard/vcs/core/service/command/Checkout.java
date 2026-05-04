package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.nio.file.Path;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.getCommit;
import static io.github.gluedtokeyboard.vcs.core.service.common.WorkingTreeService.restoreFile;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.joinCWD;

/**
 * Methods for checking out files and branches.
 */
public class Checkout extends BaseCommand {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public Checkout(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Checks out the given file from the head of the current branch.
     *
     * @param file the path of the file.
     * @throws RuntimeException if the given file isn't found in the head.
     */
    public void checkout(String file) {
        checkout(vcs.getCurrentBranchHead(), file);
    }

    /**
     * Checks out the given file from the given commit.
     *
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @param file     the path of the file.
     * @throws RuntimeException if a commit with the given commit ID does not exist, or the given file isn't found in
     *                          the commit.
     */
    public void checkout(String commitId, String file) {
        Commit commit = getCommit(commitId);
        if (commit == null) {
            throw new RuntimeException("Commit with id " + commitId + " not found");
        }
        Path path = joinCWD(file);
        if (!commit.contains(path)) {
            throw new RuntimeException("File " + file + " not found in commit " + commit.getSha1());
        }
        restoreFile(commit, path);
    }

    /**
     * Checks out the given branch.
     *
     * @param branch the branch name.
     * @throws RuntimeException if the branch does not exist, or if it is the current branch.
     */
    public void checkoutBranch(String branch) {
        if (!vcs.containsBranch(branch)) {
            throw new RuntimeException("Branch " + branch + " does not exist");
        }
        if (vcs.getCurrentBranch().equals(branch)) {
            throw new RuntimeException("Branch " + branch + " is already the current branch");
        }
        String hash = vcs.getBranchHead(branch);
        Commit commit = Commit.deserialize(hash);
        workingTreeService.applyCommitSafely(commit);
        vcs.setCurrentBranch(branch);
    }
}
