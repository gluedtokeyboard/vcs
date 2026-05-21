package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.service.command.Status.StatusRecord;
import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageUtils.modifyFile;
import static org.junit.jupiter.api.Assertions.assertEquals;

class StatusTest extends BaseCommandTest {
    @BeforeEach
    void setUp() {
        repo = RepoFixtures.repoWithMultipleBranches(ctx);
    }

    @Test
    void shouldReturnCurrentBranch() {
        StatusRecord statusRecord = repo.status();
        assertEquals("main", statusRecord.currentBranch());
    }

    @Test
    void shouldReturnAllBranches() {
        StatusRecord statusRecord = repo.status();
        assertEquals(
                Set.of("main", "development"),
                new HashSet<>(statusRecord.branches())
        );
    }

    @Test
    void shouldReturnFilesStagedForAddition() {
        String path = "/newfile.txt";
        StatusRecord statusRecord = repo
                .add(path, "hello")
                .status();
        assertEquals(
                Set.of(path),
                statusRecord.stagedAdditions()
        );
    }

    @Test
    void shouldReturnFilesStagedForDeletion() {
        String path = "/b.txt";
        StatusRecord statusRecord = repo
                .rm(path)
                .status();
        assertEquals(
                Set.of(path),
                statusRecord.stagedDeletions()
        );
    }

    @Test
    void shouldReturnUnstagedModifications() {
        String path = "/b.txt";
        modifyFile(storage, path);
        StatusRecord statusRecord = repo.status();
        assertEquals(
                Set.of(path),
                statusRecord.unstagedModifications()
        );
    }

    @Test
    void shouldReturnUnstagedDeletions() {
        String path = "/b.txt";
        storage.rm(path);
        StatusRecord statusRecord = repo.status();
        assertEquals(
                Set.of(path),
                statusRecord.unstagedDeletions()
        );
    }

    @Test
    void shouldReturnUntrackedFiles() {
        String path = "/untracked.txt";
        storage.write("hello", path);
        StatusRecord statusRecord = repo.status();
        assertEquals(
                Set.of(path),
                statusRecord.untrackedFiles()
        );
    }

    @Test
    void shouldClassifyRecreatedStagedDeletionsAsUntracked() {
        String path = "/b.txt";
        repo.rm(path);
        storage.write("hello", path);
        StatusRecord statusRecord = repo.status();
        assertEquals(
                Set.of(path),
                statusRecord.untrackedFiles()
        );
    }
}
