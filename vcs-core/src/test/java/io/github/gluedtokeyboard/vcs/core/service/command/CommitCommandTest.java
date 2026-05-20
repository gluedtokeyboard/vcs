package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.commit.CommitAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.commit.CommitUtils.head;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Commit")
class CommitCommandTest extends BaseCommandTest {
    @Nested
    @DisplayName("when the repository is empty")
    class EmptyRepository {
        @BeforeEach
        void setUp() {
            repo = RepoFixtures.emptyRepo(ctx);
        }

        @Test
        @DisplayName("updates branch head")
        void shouldUpdateBranchHead() {
            final String file = "/a.txt";
            repo
                .add(file, "hello")
                .commit("add a.txt");
            assertNotNull(repo.getCurrentBranchHead());
        }

        @Test
        @DisplayName("rejects empty commit")
        void shouldRejectEmptyCommit() {
            assertThrows(RuntimeException.class, () -> repo.commit("should fail"));
        }

        @Test
        @DisplayName("rejects empty commit message")
        void shouldRejectEmptyCommitMessage() {
            final String file = "/a.txt";
            repo.add(file, "hello");
            assertThrows(RuntimeException.class, () -> repo.commit(""));
        }

        @Test
        @DisplayName("tracks files")
        void shouldTrackFiles() {
            final String file = "/a.txt";
            repo
                .add(file, "hello")
                .commit("add a.txt");
            assertThat(head(ctx)).containsOnly("a.txt");
        }
    }

    @Nested
    @DisplayName("when the repository is not empty")
    class NonEmptyRepository {
        @BeforeEach
        void setUp() {
            repo = RepoFixtures.repoWithOneCommit(ctx);
        }

        @Test
        @DisplayName("updates tracked files")
        void shouldUpdateTrackedFiles() {
            String file = "/b.txt";
            repo
                .add(file, "hello")
                .commit("add b.txt");
            assertThat(head(ctx)).containsOnly("a.txt", "b.txt");
        }

        @Test
        @DisplayName("sets previous commit as its parent")
        void shouldSetPreviousCommitAsParent() {
            String preCommitHeadHash = repo.getCurrentBranchHead();
            String file = "/b.txt";
            repo
                .add(file, "hello")
                .commit("add b.txt");
            assertThat(head(ctx)).parent1Equals(preCommitHeadHash);
        }
    }
}
