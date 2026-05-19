package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoDsl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Init")
class InitTest extends BaseCommandTest {
    private final String repoRoot = "/";

    @BeforeEach
    void setUp() {
        repo = new RepoDsl(ctx);
    }

    @Test
    @DisplayName("initializes repository")
    void shouldInit() {
        repo.init(repoRoot);
        assertEquals(repoRoot, repo.repoRoot());
    }

    @Test
    @DisplayName("does not initialize when a repository already exists")
    void shouldNotInit() {
        repo.init(repoRoot);
        assertThrows(RuntimeException.class, () -> repo.init(repoRoot));
    }
}
