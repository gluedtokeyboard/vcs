package io.github.gluedtokeyboard.vcs.core.service.command;

import io.github.gluedtokeyboard.vcs.core.support.TestContext;
import io.github.gluedtokeyboard.vcs.core.support.repo.RepoDsl;
import io.github.gluedtokeyboard.vcs.storage.Storage;

/**
 * The base class for all command tests. Provides a {@linkplain TestContext}, convenient access to its
 * {@linkplain Storage}, and a {@link RepoDsl} for its subclasses.
 */
abstract class BaseCommandTest {
    protected final TestContext ctx = new TestContext();
    protected final Storage storage = ctx.storage();
    protected RepoDsl repo;
}
