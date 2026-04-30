package io.github.gluedtokeyboard.vcs.storage;

/**
 * Represents a path along with its type.
 * @param path a path.
 * @param type the type of the path (e.g., {@link EntryType#FILE}, {@link EntryType#DIRECTORY}).
 */
public record Entry(
        String path,
        EntryType type
) {}
