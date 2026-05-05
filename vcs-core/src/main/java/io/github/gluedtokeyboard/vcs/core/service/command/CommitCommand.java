package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.common.CommitService;
import io.github.gluedtokeyboard.vcs.core.service.common.StagingAreaService;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.Date;
import java.util.TreeMap;

/**
 * Methods for creating a commit.
 */
public class CommitCommand extends BaseCommand {
    /**
     * Used to get the latest commit of the current branch.
     */
    private final CommitService commitService;

    /**
     * Stores the given interface implementations and a {@link CommitService} object.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param commitService a {@link CommitService} object initialized with the same interface implementations as this
     *                       object.
     */
    public CommitCommand(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, CommitService commitService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.commitService = commitService;
    }

    /**
     * Creates a commit with the given message.
     * @param ctx the repository context.
     * @param message the commit message.
     */
    public void commit(VcsContext ctx, String message) {
        commit(ctx, null, message);
    }

    /**
     * Creates a commit with the given second parent and message.
     * @param ctx the repository context.
     * @param parent2 the SHA-1 hash of the second parent, or {@code null} if there is no second parent.
     * @param message the commit message.
     */
    void commit(VcsContext ctx, String parent2, String message) {
        if (ctx.vcs().stagingAreaIsEmpty()) {
            throw new RuntimeException("No changes to commit");
        }
        if (message == null || message.isEmpty()) {
            throw new RuntimeException("Please enter a message");
        }
        Commit commit = createCommit(ctx, parent2, message);
        ctx.vcs().setCurrentBranchHead(commit.getSha1());
        ctx.vcs().clearStagingArea();
        commitStore.store(ctx.repoRoot(), commit);
    }

    /**
     * Creates a new commit with the given second parent, message, and the staged files.
     * @param ctx the repository context.
     * @param parent2 the SHA-1 hash of the second parent, or {@code null} if there is no second parent.
     * @param message the commit message.
     * @return the newly created commit.
     */
    private Commit createCommit(VcsContext ctx, String parent2, String message) {
        Commit newCommit;
        Date timestamp = new Date();
        TreeMap<String, String> stagedFiles = ctx.vcs().getStagedFilesClone();
        Commit latestCommit = commitService.getLatestCommitCurrentBranch(ctx);
        if (latestCommit == null) {
            newCommit = new Commit(message, timestamp, stagedFiles);
        } else {
            TreeMap<String, String> files = latestCommit.getFilesClone();
            updateFiles(files, stagedFiles);
            if (parent2 == null) {
                newCommit = new Commit(message, timestamp, latestCommit.getSha1(), files);
            } else {
                newCommit = new Commit(message, timestamp, latestCommit.getSha1(), parent2, files);
            }
        }
        return newCommit;
    }

    /**
     * Updates the files of the parent commit in place with the staged files.
     * @param parentFiles the files of the parent commit.
     * @param stagedFiles the staged files.
     */
    private static void updateFiles(TreeMap<String, String> parentFiles, TreeMap<String, String> stagedFiles) {
        stagedFiles.forEach((key, value) -> {
            if (value.equals(StagingAreaService.DELETION_MARKER)) {
                parentFiles.remove(key);
            } else {
                parentFiles.put(key, value);
            }
        });
    }
}
