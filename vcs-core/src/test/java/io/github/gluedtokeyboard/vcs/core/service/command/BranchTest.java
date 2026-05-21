package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.repo.RepoFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.gluedtokeyboard.vcs.core.support.repo.RepoAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BranchTest extends BaseCommandTest {
    @BeforeEach
    void setUp() {
        repo = RepoFixtures.emptyRepo(ctx);
    }

    @Test
    void shouldCreateBranch() {
        String newBranch = "new branch";
        repo.createBranch(newBranch);
        assertThat(repo).containsBranch(newBranch);
    }

    @Test
    void shouldThrowWhileCreatingBranchThatExists() {
        String currentBranch = repo.getCurrentBranch();
        assertThrows(RuntimeException.class, () -> repo.createBranch(currentBranch));
    }

    @Test
    void shouldRemoveBranch() {
        String newBranch = "new branch";
        repo
            .createBranch(newBranch)
            .removeBranch(newBranch);
        assertThat(repo).doesNotContainBranch(newBranch);
    }

    @Test
    void shouldNotRemoveCurrentBranch() {
        String currentBranch = repo.getCurrentBranch();
        assertThrows(RuntimeException.class, () -> repo.removeBranch(currentBranch));
    }

    @Test
    void shouldThrowWhileRemovingNonExistentBranch() {
        String nonExistentBranch = "does not exist";
        assertThrows(RuntimeException.class, () -> repo.removeBranch(nonExistentBranch));
    }
}
