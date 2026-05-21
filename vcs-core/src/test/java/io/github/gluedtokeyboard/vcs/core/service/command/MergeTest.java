package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.repo.RepoAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Merge")
class MergeTest extends BaseCommandTest {
    private String initialBranch;
    private final String otherBranch = "development";

    @Nested
    @DisplayName("when the repository has a linear commit history")
    class LinearCommitHistory {
        @BeforeEach
        void setUp() {
            repo = RepoFixtures.repoWithMultipleBranches(ctx);
            initialBranch = repo.getCurrentBranch();
        }

        @Test
        @DisplayName("throws when merging the current branch")
        void shouldThrowWhenMergingTheCurrentBranch() {
            assertThrows(RuntimeException.class, () -> repo.merge(initialBranch));
        }

        @Test
        @DisplayName("throws when merging a non-existent branch")
        void shouldThrowWhenMergingNonExistentBranch() {
            String branch = "invalid";
            assertThrows(RuntimeException.class, () -> repo.merge(branch));
        }

        @Test
        @DisplayName("throws when the given branch is an ancestor of the current branch")
        void shouldThrowWhenGivenBranchIsAnAncestorOfTheCurrentBranch() {
            assertThrows(RuntimeException.class, () -> repo.merge(otherBranch));
        }

        @Nested
        @DisplayName("when the current branch is an ancestor of the given branch")
        class CurrentBranchIsAnAncestorOfTheGivenBranch {
            @BeforeEach
            void setUp() {
                repo.checkoutBranch(otherBranch);
            }

            @Test
            @DisplayName("should fast forward files")
            void shouldFastForwardFiles() {
                try {
                    repo.merge(initialBranch);
                } catch (RuntimeException e) {
                /*
                This throws a RuntimeException to indicate that the current branch has been fast-forwarded, so do
                nothing.
                */
                }
                assertThat(storage)
                        .doesNotContain("/a.txt")
                        .contains("/b.txt", "hello world")
                        .contains("/c.txt", "hello")
                        .contains("/d.txt", "hello");
            }

            @Test
            @DisplayName("should fast forward branch pointer")
            void shouldFastForwardBranchPointer() {
                try {
                    repo.merge(initialBranch);
                } catch (RuntimeException e) {
                /*
                This throws a RuntimeException to indicate that the current branch has been fast-forwarded, so do
                nothing.
                */
                }
                String expectedBranchHead = repo.getBranchHead(initialBranch);
                assertThat(repo).currentBranchHeadEquals(expectedBranchHead);
            }
        }
    }
}
