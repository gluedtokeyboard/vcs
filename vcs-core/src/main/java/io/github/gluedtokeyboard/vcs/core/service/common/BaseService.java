package io.github.gluedtokeyboard.vcs.core.service.common;

import io.github.gluedtokeyboard.vcs.core.model.Vcs;

/**
 * The base class for all common service classes.
 */
abstract class BaseService {
    /**
     * The current repository.
     */
    protected final Vcs vcs;

    /**
     * Stores the current repository.
     * @param vcs the current repository object.
     */
    protected BaseService(Vcs vcs) {
        this.vcs = vcs;
    }
}
