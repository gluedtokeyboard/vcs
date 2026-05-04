package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.model.Vcs;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import io.github.gluedtokeyboard.vcs.util.FileSystem;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Iterator;
import java.util.Queue;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.exists;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.ls;
import static io.github.gluedtokeyboard.vcs.util.Serialization.deserialize;

/**
 * Commit methods.
 */
public final class CommitService extends BaseService {
    /**
     * The representation of a null commit.
     */
    public static final String NULL_COMMIT = "";

    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public CommitService(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Returns the destination for the blob when a commit is made.
     * @param sha1 the SHA-1 hash of the blob.
     * @return the blob's destination when a commit is made.
     */
    public static String getBlobDestinationCommit(String sha1) {
        return join(Vcs.getBlobsFolder(), sha1);
    }

    /**
     * Returns the latest commit in the current branch.
     * @return the latest commit in the current branch, or {@code null} if there are no commits.
     */
    public Commit getLatestCommitCurrentBranch() {
        String latestHash = getLatestHashCurrentBranch();
        if (latestHash == null) {
            return null;
        }
        return Commit.deserialize(latestHash);
    }

    /**
     * Returns the hash of the latest commit in the current branch.
     * @return the hash of the latest commit in the current branch, or {@code null} if there are no commits in the
     * current branch.
     */
    private String getLatestHashCurrentBranch() {
        return vcs.getCurrentBranchHead();
    }

    /**
     * Returns the commit matching the given commit ID.
     * @param commitId a prefix of the SHA-1 hash of the commit.
     * @return the commit matching the given commit ID, or {@code null} if the commit isn't found.
     */
    public static Commit getCommit(String commitId) {
        Commit[] commit = {null};
        Path directory = Commit.getDestinationFolder(commitId);
        if (exists(directory)) {
            ls(directory).forEach(path -> {
                String basename = FileSystem.basename(path);
                if (basename.startsWith(commitId)) {
                    commit[0] = deserialize(path, Commit.class);
                }
            });
        }
        return commit[0];
    }

    /**
     * Deserializes a committed {@link Blob}.
     * @param sha1 the SHA-1 hash of the {@link Blob}.
     * @return the deserialized {@link Blob}.
     */
    public static Blob getCommittedBlob(String sha1) {
        Path blobLocation = CommitService.getBlobDestinationCommit(sha1);
        return Blob.deserialize(blobLocation);
    }

    /**
     * Yields the SHA-1 hashes of the commit and its ancestors in BFS order.
     * @param commit the starting {@linkplain Commit}.
     * @return an iterable that yields the SHA-1 hashes of the commit and its ancestors in BFS order.
     */
    public static Iterable<String> getCommitsInBFSOrder(Commit commit) {
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
                    Commit commit = Commit.deserialize(sha1);
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
}
