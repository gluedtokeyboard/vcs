package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.commit.CommitUtils.getCommit;
import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageUtils.modifyFile;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Checkout")
class CheckoutFileTest extends BaseCommandTest {
    @BeforeEach
    void setUp() {
        repo = RepoFixtures.repoWithMultipleBranches(ctx);
    }

    @Test
    @DisplayName("checks out file from head")
    void shouldCheckoutFileFromHead() {
        String path = "/b.txt";
        String originalContents = storage.readString(path);
        modifyFile(storage, path);
        repo.checkout(path);
        assertThat(storage).contains(path, originalContents);
    }

    @Test
    @DisplayName("throws for non-existent file")
    void shouldThrowForNonExistentFile() {
        String path = "/invalid";
        assertThrows(RuntimeException.class, () -> repo.checkout(path));
    }

    @Test
    @DisplayName("checks out file from commit")
    void shouldCheckoutFileFromCommit() {
        String path = "/a.txt";
        String commitHash = getCommit(ctx, 1).getSha1();
        repo.checkout(commitHash, path);
        String expectedContents = "hello";
        assertThat(storage).contains(path, expectedContents);
    }

    @Test
    @DisplayName("checks out file from commit using commit prefix")
    void shouldCheckoutFileFromCommitUsingCommitPrefix() {
        String path = "/a.txt";
        String commitPrefix = getCommit(ctx, 1)
                .getSha1()
                .substring(0, 7);
        repo.checkout(commitPrefix, path);
        String expectedContents = "hello";
        assertThat(storage).contains(path, expectedContents);
    }

    @Test
    @DisplayName("throws for non-existent commit prefix")
    void shouldThrowForNonExistentCommitPrefix() {
        String path = "/a.txt";
        String commitPrefix = "invalid";
        assertThrows(RuntimeException.class, () -> repo.checkout(commitPrefix, path));
    }
}
