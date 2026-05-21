package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.commit.CommitUtils.getCommit;
import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Reset")
class ResetTest extends BaseCommandTest {
    private String commitHash;

    @BeforeEach
    void setUp() {
        repo = RepoFixtures.repoWithMultipleBranches(ctx);
        commitHash = getCommit(ctx, 3).getSha1();
    }

    @Nested
    @DisplayName("when the working tree is clean")
    class CleanWorkingTree {
        @Test
        @DisplayName("restores the working tree")
        void shouldRestoreWorkingTree() {
            repo.reset(commitHash);
            assertSuccess();
        }

        @Test
        @DisplayName("resets using commit prefix")
        void shouldResetUsingCommitPrefix() {
            String commitPrefix = commitHash.substring(0, 7);
            repo.reset(commitPrefix);
            assertSuccess();
        }

        @Test
        @DisplayName("throws for non-existent commit hash")
        void shouldThrowForNonExistentCommitHash() {
            String commitHash = "invalid";
            assertThrows(RuntimeException.class, () -> repo.reset(commitHash));
        }
    }

    @Nested
    @DisplayName("when the working tree is dirty")
    class DirtyWorkingTree extends BaseDirtyWorkingTreeTest {
        @BeforeEach
        void setUp() {
            init(storage, repo);
        }

        @Override
        void runCommand() {
            repo.reset(commitHash);
        }

        @Override
        void assertSuccess() {
            ResetTest.this.assertSuccess();
        }
    }

    private void assertSuccess() {
        assertThat(storage)
                .contains("/a.txt", "hello")
                .contains("/b.txt", "hello")
                .doesNotContain("/d.txt");
    }
}
