package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.model.Commit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Log")
class LogTest extends BaseCommandTest<Log> {
    /**
     * Initializes the command to test.
     *
     * @param commands the object containing all the commands.
     * @return the command to test obtained from {@code commands}.
     */
    @Override
    protected Log createCommand(Commands commands) {
        return commands.log();
    }

    @Nested
    @DisplayName("when the repository is empty")
    class EmptyRepository {
        @BeforeEach
        void setup() {
            init(StorageFactory.emptyRepository());
        }

        @Test
        @DisplayName("returns an empty list")
        void shouldReturnEmptyList() {
            assertTrue(cmd.log(ctx).isEmpty());
        }
    }

    @Nested
    @DisplayName("when the repository has commits")
    class HasCommits {
        @BeforeEach
        void setUp() {
            init(StorageFactory.repositoryWithTwoCommits());
        }

        @Test
        @DisplayName("returns commits in reverse chronological order")
        void shouldReturnCommitsInReverseChronologicalOrder() {
            ArrayList<Commit> commits = cmd.log(ctx);
            assertEquals(
                    List.of("update a.txt", "add a.txt"),
                    commits.stream().map(Commit::getMessage).toList()
            );
        }
    }

}
