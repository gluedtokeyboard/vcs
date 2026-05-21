package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.repo.RepoAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Checkout Branch")
class CheckoutBranchTest extends BaseCommandTest {
    @BeforeEach
    void setUp() {
        repo = RepoFixtures.repoWithMultipleBranches(ctx);
    }

    @Nested
    @DisplayName("when the working tree is clean")
    class CleanWorkingTree {
        @Test
        @DisplayName("switches the branch")
        void shouldSwitchBranch() {
            String branch = "development";
            repo.checkoutBranch(branch);
            assertThat(repo).currentBranchEquals(branch);
        }

        @Test
        @DisplayName("resets working tree")
        void shouldResetWorkingTree() {
            String branch = "development";
            repo.checkoutBranch(branch);
            assertSuccess();
        }

        @Test
        @DisplayName("throws for non-existent branch")
        void shouldThrowForNonExistentBranch() {
            String branch = "invalid";
            assertThrows(RuntimeException.class, () -> repo.checkoutBranch(branch));
        }

        @Test
        @DisplayName("throws when checking out the current branch")
        void shouldThrowWhenCheckingOutCurrentBranch() {
            String branch = repo.getCurrentBranch();
            assertThrows(RuntimeException.class, () -> repo.checkoutBranch(branch));
        }
    }

    @Nested
    @DisplayName("when the working tree is dirty")
    class DirtyWorkingTree extends BaseDirtyWorkingTreeTest {
        @BeforeEach
        void setUp() {
            init(storage, repo);
        }

        /**
         * Runs the command to test.
         */
        @Override
        void runCommand() {
            repo.checkoutBranch("development");
        }

        /**
         * Asserts that the command has run successfully.
         */
        @Override
        void assertSuccess() {
            CheckoutBranchTest.this.assertSuccess();
        }
    }

    private void assertSuccess() {
        assertThat(storage)
                .contains("/a.txt", "hello")
                .contains("/b.txt", "hello")
                .doesNotContain("/d.txt");
    }
}
