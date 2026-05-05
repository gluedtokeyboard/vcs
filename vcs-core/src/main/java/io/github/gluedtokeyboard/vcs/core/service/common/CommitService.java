package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Commit methods.
 */
public final class CommitService extends BaseService {
    /**
     * The representation of a null commit.
     */
    public static final String NULL_COMMIT = "";

    /**
     * Used to convert repository-relative paths to absolute paths.
     */
    private final GeneralService generalService;

    /**
     * Stores the given interface implementations, and a {@link GeneralService} object.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     * @param generalService a {@link GeneralService} object initialized with the same interface implementations as this
     *                       object.
     */
    public CommitService(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore, GeneralService generalService) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
        this.generalService = generalService;
    }

    /**
     * Returns the latest commit in the current branch.
     * @param ctx the repository context.
     * @return the latest commit in the current branch, or {@code null} if there are no commits.
     */
    public Commit getLatestCommitCurrentBranch(VcsContext ctx) {
        String latestHash = getLatestHashCurrentBranch(ctx);
        if (latestHash == null) {
            return null;
        }
        return commitStore.load(ctx.repoRoot(), latestHash);
    }

    /**
     * Returns the hash of the latest commit in the current branch.
     * @param ctx the repository context.
     * @return the hash of the latest commit in the current branch, or {@code null} if there are no commits in the
     * current branch.
     */
    private String getLatestHashCurrentBranch(VcsContext ctx) {
        return ctx.vcs().getCurrentBranchHead();
    }

    /**
     * Yields the SHA-1 hashes of the commit and its ancestors in BFS order.
     * @param ctx the repository context.
     * @param commit the starting {@linkplain Commit}.
     * @return an iterable that yields the SHA-1 hashes of the commit and its ancestors in BFS order.
     */
    public Iterable<String> getCommitsInBFSOrder(VcsContext ctx, Commit commit) {
        return () -> {
            Queue<String> fringe = new ArrayDeque<>();
            fringe.add(commit.getSha1());
            return new Iterator<>() {
                /**
                 * Checks if the fringe is not empty.
                 * @return a boolean indicating whether there are more hashes to yield.
                 */
                @Override
                public boolean hasNext() {
                    return !fringe.isEmpty();
                }

                /**
                 * Returns the next commit hash in BFS order, and adds its parents to the fringe.
                 * @return the next commit hash in BFS order.
                 */
                @Override
                public String next() {
                    String sha1 = fringe.remove();
                    Commit commit = commitStore.load(ctx.repoRoot(), sha1);
                    String parent1 = commit.getParent1();
                    String parent2 = commit.getParent2();
                    if (!parent1.equals(NULL_COMMIT)) {
                        fringe.add(parent1);
                    }
                    if (!parent2.equals(NULL_COMMIT)) {
                        fringe.add(parent2);
                    }
                    return sha1;
                }
            };
        };
    }

    /**
     * Returns the absolute paths of the files tracked by the given commit.
     * @param repoRoot the repository root.
     * @param commit a {@linkplain Commit} object.
     * @return the absolute paths of the files tracked by the given commit.
     */
    public Set<String> getCommitFilesAbsolutePaths(String repoRoot, Commit commit) {
        return commit.getCommitFiles()
                .map(repoRelativePath -> generalService.getAbsolutePath(repoRoot, repoRelativePath))
                .collect(Collectors.toSet());
    }
}
