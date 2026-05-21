package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoDsl;
import io.github.gluedtokeyboard.vcs.storage.Storage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageUtils.modifyFile;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The base class for all dirty working tree tests. Provides dirty working tree tests for its subclasses.
 */
abstract class BaseDirtyWorkingTreeTest {
    private RepoDsl repo;
    private Storage storage;

    void init(Storage storage, RepoDsl repo) {
        this.repo = repo;
        this.storage = storage;
    }

    /**
     * Runs the command to test.
     */
    abstract void runCommand();

    /**
     * Asserts that the command has run successfully.
     */
    abstract void assertSuccess();

    @Test
    @DisplayName("throws when the staging area is not empty")
    void shouldThrowWhenStagingAreaIsNotEmpty() {
        String path = "/b.txt";
        modifyFile(storage, path);
        repo.add(path);
        assertThrows(RuntimeException.class, this::runCommand);
    }

    @Test
    @DisplayName("throws when there are unstaged modifications")
    void shouldThrowWhenThereAreUnstagedModifications() {
        String path = "/b.txt";
        modifyFile(storage, path);
        assertThrows(RuntimeException.class, this::runCommand);
    }

    @Test
    @DisplayName("throws when there are untracked conflicts")
    void shouldThrowWhenThereAreUntrackedConflicts() {
        String path = "/a.txt";
        storage.write("", path);
        assertThrows(RuntimeException.class, this::runCommand);
    }

    @Test
    @DisplayName("runs successfully when there are non-conflicting untracked files")
    void shouldRunSuccessfullyWhenThereAreNonConflictingUntrackedFiles() {
        String untrackedFile = "/untracked.txt";
        storage.write("", untrackedFile);
        runCommand();
        assertSuccess();
    }

    @Test
    @DisplayName("does not overwrite non-conflicting untracked files")
    void shouldNotOverwriteNonConflictingUntrackedFiles() {
        String untrackedFile = "/untracked.txt";
        String contents = "hello";
        storage.write(contents, untrackedFile);
        runCommand();
        assertThat(storage).contains(untrackedFile, contents);
    }
}
