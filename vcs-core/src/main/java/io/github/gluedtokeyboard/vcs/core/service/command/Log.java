package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Blob;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.storage.layout.VcsLayout;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.contentaddressable.ContentAddressableStore;
import io.github.gluedtokeyboard.vcs.core.storage.objectstore.vcs.VcsStore;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.util.ArrayList;

import static io.github.gluedtokeyboard.vcs.core.service.common.CommitService.NULL_COMMIT;

/**
 * Methods for obtaining the repository logs.
 */
public class Log extends BaseCommand {
    /**
     * Stores the given interface implementations.
     *
     * @param storage     a {@link Storage} implementation.
     * @param vcsLayout   a {@link VcsLayout} implementation.
     * @param vcsStore    a {@link VcsStore} implementation.
     * @param blobStore   a {@link ContentAddressableStore<Blob>} implementation.
     * @param commitStore a {@link ContentAddressableStore<Commit>} implementation.
     */
    public Log(
            Storage storage, VcsLayout vcsLayout, VcsStore vcsStore, ContentAddressableStore<Blob> blobStore,
            ContentAddressableStore<Commit> commitStore) {
        super(storage, vcsLayout, vcsStore, blobStore, commitStore);
    }

    /**
     * Returns commits starting from the head, following their first parents.
     * @return An {@linkplain ArrayList} of commits starting from the head to the earliest reachable commit.
     */
    public ArrayList<Commit> log() {
        ArrayList<Commit> commits = new ArrayList<>();
        String currentHash = vcs.getCurrentBranchHead();
        while (!(currentHash == null || currentHash.equals(NULL_COMMIT))) {
            Commit commit = Commit.deserialize(currentHash);
            commits.addLast(commit);
            currentHash = commit.getParent1();
        }
        return commits;
    }
}
