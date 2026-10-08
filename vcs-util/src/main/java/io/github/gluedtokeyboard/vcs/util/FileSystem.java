package io.github.gluedtokeyboard.vcs.util;

import java.nio.file.*;

/**
 * File system utils.
 */
public final class FileSystem {
    /**
     * Joins the given parts to the given path.
     *
     * @param base  The base path.
     * @param parts The parts to join to the path.
     * @return the given parts joined to the base path.
     */
    public static String join(String base, String... parts) {
        Path joinedPath = Path.of(base);
        for (String part : parts) {
            joinedPath = joinedPath.resolve(part);
        }
        return joinedPath.toString();
    }

    /**
     * Joins the given paths to the current working directory.
     *
     * @param parts the parts to join to the current working directory.
     * @return the given parts joined to the current working directory.
     */
    public static String joinCWD(String... parts) {
        return join(getCWD(), parts);
    }

    /**
     * Returns the absolute path of the current working directory.
     *
     * @return the current working directory.
     */
    public static String getCWD() {
        return Path.of("").toAbsolutePath().toString();
    }

    /**
     * Returns the given absolute path relative to the given base path.
     * @param path an absolute path.
     * @param base a base path.
     * @return the given absolute path relative to the given base path.
     */
    public static String getRelativePath(String path, String base) {
        return Path.of(base).relativize(Path.of(path)).toString();
    }

    /**
     * Returns the given absolute path relative to the current working directory.
     * @param path an absolute path.
     * @return the given absolute path relative to the current working directory.
     */
    public static String getRelativePathCWD(String path) {
        return getRelativePath(path, getCWD());
    }
}
