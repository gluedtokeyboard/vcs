package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.*;

import static io.github.gluedtokeyboard.vcs.core.support.commit.CommitUtils.head;
import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.repo.RepoAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.commit.CommitAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Nested
    @DisplayName("when the repository has a non-linear commit history")
    class NonLinearCommitHistory {
        @BeforeEach
        void setUp() {
            repo = RepoFixtures.repoWithNonLinearHistory(ctx);
            initialBranch = repo.getCurrentBranch();
        }

        @Nested
        @DisplayName("when the working tree is clean")
        class CleanWorkingTree {
            @Nested
            @DisplayName("when there are no merge conflicts")
            class WithoutMergeConflicts {
                @Test
                @DisplayName("copies new files from the given branch")
                void shouldCopyNewFilesFromTheGivenBranch() {
                    repo.merge(otherBranch);
                    assertThat(storage).contains("/e.txt", "hello");
                }

                @Test
                @DisplayName("copies files modified in the given branch")
                void shouldCopyFilesModifiedInGivenBranch() {
                    repo
                        .checkoutBranch(otherBranch)
                        .merge(initialBranch);
                    assertThat(storage).contains("/b.txt", "hello world");
                }

                @Test
                @DisplayName("keeps files modified only in the current branch")
                void shouldKeepFilesModifiedOnlyInCurrentBranch() {
                    repo.merge(otherBranch);
                    assertThat(storage).contains("/b.txt", "hello world");
                }

                @Test
                @DisplayName("does not copy files from the given branch that are deleted in the current branch")
                void shouldNotCopyFilesDeletedInCurrentBranch() {
                    repo.merge(otherBranch);
                    assertThat(storage).doesNotContain("/a.txt");
                }

                @Test
                @DisplayName("deletes files deleted in the given branch")
                void shouldDeleteFilesDeletedInTheGivenBranch() {
                    repo
                        .checkoutBranch(otherBranch)
                        .merge(initialBranch);
                    assertThat(storage).doesNotContain("/a.txt");
                }

                @Test
                @DisplayName("does not create files deleted in both branches")
                void shouldNotCreateFilesDeletedInBothBranches() {
                    repo
                        .checkoutBranch(otherBranch)
                        .rm("/a.txt")
                        .commit("rm a.txt")
                        .merge(initialBranch);
                    assertThat(storage).doesNotContain("/a.txt");
                }

                @Test
                @DisplayName(
                        "does not throw a merge conflict for files with the same modifications in both the branches")
                void shouldNotThrowMergeConflictForFilesWithSameModificationsInBothBranches() {
                    String path = "/b.txt";
                    String contents = "hello world";
                    repo
                        .checkoutBranch(otherBranch)
                        .add(path, contents)
                        .commit("update b.txt")
                        .merge(initialBranch);
                    assertThat(storage).contains(path, contents);
                }

                @Test
                @DisplayName("creates merge commit")
                void shouldCreateMergeCommit() {
                    String initialBranchHead = repo.getCurrentBranchHead();
                    String otherBranchHead = repo.getBranchHead(otherBranch);
                    repo.merge(otherBranch);
                    assertThat(head(ctx))
                            .parent1Equals(initialBranchHead)
                            .parent2Equals(otherBranchHead);
                }
            }

            @Nested
            @DisplayName("when there are merge conflicts")
            class WithMergeConflicts {
                private String initialBranchHead;

                @BeforeEach
                void setUp() {
                    initialBranchHead = repo.getCurrentBranchHead();
                }

                @Test
                @DisplayName("throws for files modified differently in both the branches")
                void shouldThrowForFilesModifiedDifferentlyInBothBranches() {
                    repo
                        .checkoutBranch(otherBranch)
                        .add("/b.txt", "Hello, world!")
                        .commit("update b.txt")
                        .checkoutBranch(initialBranch);
                    assertThrows(RuntimeException.class, () -> repo.merge(otherBranch));
                }

                @Test
                @DisplayName("throws for files deleted in the current branch but modified in the given branch")
                void shouldThrowForFilesDeletedInCurrentBranchButModifiedInGivenBranch() {
                    repo
                        .checkoutBranch(otherBranch)
                        .add("/a.txt", "hello world")
                        .commit("update a.txt")
                        .checkoutBranch(initialBranch);
                    assertThrows(RuntimeException.class, () -> repo.merge(otherBranch));
                }

                @Test
                @DisplayName("throws for files deleted in the given branch but modified in the current branch")
                void shouldThrowForFilesDeletedInTheGivenBranchButModifiedInCurrentBranch() {
                    repo
                        .checkoutBranch(otherBranch)
                        .rm("/b.txt")
                        .commit("rm b.txt")
                        .checkoutBranch(initialBranch);
                    assertThrows(RuntimeException.class, () -> repo.merge(otherBranch));
                }

                @AfterEach
                void verifyCommitParents() {
                    String otherBranchHead = repo.getBranchHead(otherBranch);
                    assertThat(head(ctx))
                            .parent1Equals(initialBranchHead)
                            .parent2Equals(otherBranchHead);
                }

                @AfterEach
                void verifyNoUnstagedModifications() {
                    String message = "The repository contains unstaged modifications after a merge with conflicts. " +
                            "Check if the merge conflict blocks have been committed.";
                    assertTrue(repo.status().unstagedModifications().isEmpty(), message);
                }
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
                repo.merge(otherBranch);
            }

            /**
             * Asserts that the command has run successfully.
             */
            @Override
            void assertSuccess() {
                assertThat(storage)
                        .doesNotContain("/a.txt")
                        .contains("/b.txt", "hello world")
                        .contains("/c.txt", "hello")
                        .contains("/d.txt", "hello")
                        .contains("/e.txt", "hello");
            }
        }
    }
}
