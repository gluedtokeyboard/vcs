package io.github.gluedtokeyboard.vcs.core.context;

import io.github.gluedtokeyboard.vcs.core.model.Vcs;

/**
 * Provides the context for the repository.
 *
 * @param vcs      The repository object.
 * @param repoRoot The root of the repository.
 */
public record VcsContext(Vcs vcs, String repoRoot) {
}
