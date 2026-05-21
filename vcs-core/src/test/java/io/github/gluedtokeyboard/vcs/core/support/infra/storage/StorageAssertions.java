package io.github.gluedtokeyboard.vcs.core.support.infra.storage;

import io.github.gluedtokeyboard.vcs.storage.Storage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Assertions for {@link Storage}.
 */
public class StorageAssertions {
    private final Storage storage;

    private StorageAssertions(final Storage storage) {
        this.storage = storage;
    }

    public static StorageAssertions assertThat(final Storage storage) {
        return new StorageAssertions(storage);
    }

    public StorageAssertions contains(final String path) {
        String message = "Expected storage to contain " + path;
        assertTrue(storage.exists(path), message);
        return this;
    }

    public StorageAssertions contains(final String path, final String expectedContents) {
        String message = "Expected storage to contain " + path + " with contents: " + expectedContents;
        String actualContents = storage.readString(path);
        assertEquals(expectedContents, actualContents, message);
        return this;
    }

    public StorageAssertions doesNotContain(final String path) {
        String message = "Expected storage to not contain " + path;
        assertFalse(storage.exists(path), message);
        return this;
    }
}
