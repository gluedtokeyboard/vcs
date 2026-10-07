package io.github.gluedtokeyboard.vcs.util;

import java.nio.file.*;

/**
 * File system utils.
 */
public final class FileSystem {
    /**
     * Joins the given parts to the given path.
     *
     * @param path  The base path.
     * @param parts The parts to join to the path.
     * @return A {@link Path} object containing the given parts joined to the base path.
     */
    public static Path join(Path path, String... parts) {
        for (String part : parts) {
            path = path.resolve(part);
        }
        return path;
    }

    /**
     * Joins the given paths to the current working directory.
     *
     * @param parts the parts to join to the current working directory.
     * @return the given parts joined to the current working directory.
     */
    public static Path joinCWD(String... parts) {
        return join(getCWD(), parts);
    }

    /**
     * Returns the absolute path of the current working directory.
     *
     * @return A {@link Path} object containing the current working directory.
     */
    public static Path getCWD() {
        return Path.of("").toAbsolutePath();
    }

    /**
     * Returns the given absolute path relative to the given base path.
     * @param path an absolute path.
     * @param base a base path.
     * @return the given absolute path relative to the given base path.
     */
    public static Path getRelativePath(Path path, Path base) {
        return base.relativize(path);
    }

    /**
     * Returns the given absolute path relative to the current working directory.
     * @param path an absolute path.
     * @return the given absolute path relative to the current working directory.
     */
    public static String getRelativePathCWD(String path) {
        return getRelativePath(Path.of(path), getCWD()).toString();
    }
}
