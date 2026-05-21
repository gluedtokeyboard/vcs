package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.repo.RepoAssertions.assertThat;
import static io.github.gluedtokeyboard.vcs.core.support.infra.storage.StorageAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Rm")
class RmTest extends BaseCommandTest {
    @BeforeEach
    void setUp() {
        repo = RepoFixtures.repoWithOneCommit(ctx);
    }

    @Nested
    @DisplayName("when the file is tracked")
    class TrackedFile {
        private final String path = "/a.txt";
        private final String repoRelativePath = "a.txt";

        @Test
        @DisplayName("removes file")
        void shouldRemoveFile() {
            repo.rm(path);
            assertThat(storage).doesNotContain(path);
        }

        @Test
        @DisplayName("stages removal")
        void shouldStageRemoval() {
            repo.rm(path);
            assertThat(repo).isStaged(repoRelativePath);
        }
    }

    @Nested
    @DisplayName("when the file is untracked")
    class UntrackedFile {
        private final String path = "/b.txt";
        private final String repoRelativePath = "b.txt";

        @BeforeEach
        void setUp() {
            storage.write("hello world", path);
        }

        @Nested
        @DisplayName("and staged")
        class Staged {
            @BeforeEach
            void setUp() {
                repo.add(path);
            }

            @Test
            @DisplayName("unstages file")
            void shouldUnstageFile() {
                repo.rm(path);
                assertThat(repo).isNotStaged(repoRelativePath);
            }

            @Test
            @DisplayName("does not delete file")
            void shouldNoteDeleteFile() {
                repo.rm(path);
                assertThat(storage).contains(path);
            }
        }

        @Nested
        @DisplayName("and not staged")
        class NotStaged {
            @Test
            @DisplayName("throws exception")
            void shouldThrow() {
                assertThrows(RuntimeException.class, () -> repo.rm(path));
            }

            @Test
            @DisplayName("does not delete file")
            void shouldNotDeleteUntrackedFile() {
                try {
                    repo.rm(path);
                } catch (RuntimeException ignored) {
                    /* Do nothing. */
                    /* (rm throws an exception when removing an untracked file) */
                }
                assertThat(storage).contains(path);
            }
        }
    }
}
