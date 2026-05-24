package io.github.gluedtokeyboard.vcs.util;

import com.github.f4b6a3.uuid.UuidCreator;

import java.util.UUID;

/**
 * UUID generator.
 */
public final class Uuid {
    /**
     * Generates a UUIDv7.
     *
     * @return a UUIDv7
     */
    public static UUID uuidV7() {
        return UuidCreator.getTimeOrderedEpoch();
    }
}
