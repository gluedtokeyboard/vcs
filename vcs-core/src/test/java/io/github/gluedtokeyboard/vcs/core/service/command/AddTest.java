package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.repo.RepoAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Add")
class AddTest extends BaseCommandTest {
    private final String path = "/a.txt";
    private final String repoRelativePath = "a.txt";

    @Nested
    @DisplayName("when the repository is empty")
    class EmptyRepository {
        @BeforeEach
        void setUp() {
            repo = RepoFixtures.emptyRepo(ctx);
        }

        @Test
        @DisplayName("stages file")
        void shouldStageFile() {
            repo.add(path, "hello world");
            assertThat(repo).isStaged(repoRelativePath);
        }

        @Test
        @DisplayName("throws exception for non-existent file")
        void shouldThrowForNonExistentFile() {
            assertThrows(RuntimeException.class, () -> repo.add(path));
        }
    }
}
