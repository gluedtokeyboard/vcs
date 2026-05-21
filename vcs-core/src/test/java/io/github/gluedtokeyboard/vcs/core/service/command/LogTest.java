package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Log")
class LogTest extends BaseCommandTest {
    @Nested
    @DisplayName("when the repository is empty")
    class EmptyRepository {
        @BeforeEach
        void setup() {
            repo = RepoFixtures.emptyRepo(ctx);
        }

        @Test
        @DisplayName("returns an empty list")
        void shouldReturnEmptyList() {
            assertTrue(repo.log().isEmpty());
        }
    }

    @Nested
    @DisplayName("when the repository has commits")
    class HasCommits {
        @BeforeEach
        void setUp() {
            repo = RepoFixtures.repoWithMultipleCommits(ctx);
        }

        @Test
        @DisplayName("returns commits in reverse chronological order")
        void shouldReturnCommitsInReverseChronologicalOrder() {
            ArrayList<Commit> commits = repo.log();
            List<String> expectedMessages = List.of(
                    "add c.txt",
                    "add b.txt",
                    "add a.txt"
            );
            List<String> actualMessages = commits
                    .stream()
                    .map(Commit::getMessage)
                    .toList();
            assertEquals(expectedMessages, actualMessages);
        }
    }
}
