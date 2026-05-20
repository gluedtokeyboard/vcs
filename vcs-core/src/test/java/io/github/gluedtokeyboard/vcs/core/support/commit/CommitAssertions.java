package io.github.gluedtokeyboard.vcs.core.support.commit;

import io.github.gluedtokeyboard.vcs.core.model.Commit;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Assertions for {@linkplain Commit} properties.
 */
public final class CommitAssertions {
    private final Commit commit;

    private CommitAssertions(Commit commit) {
        this.commit = commit;
    }

    public static CommitAssertions assertThat(final Commit commit) {
        return new CommitAssertions(commit);
    }

    public CommitAssertions containsOnly(final String... files) {
        Set<String> actualFiles = commit.getCommitFiles();
        Set<String> expectedFiles = Set.of(files);
        String message = "Commit files mismatch";
        assertEquals(expectedFiles, actualFiles, message);
        return this;
    }
}
