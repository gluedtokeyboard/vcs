package vcs.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File system utils.
 */
public final class FileSystem {
    /**
     * Checks if the given path exists.
     * @param path The path of a folder or a file.
     * @return {@code true} if the path exists; {@code false} otherwise.
     */
    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /**
     * Creates the given folder along with the required intermediate folders.
     * @param folder The path of the folder.
     */
    public static void mkdir(Path folder) {
        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Joins the given parts to the given path.
     * @param path The base path.
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
     * @param parts the parts to join to the current working directory.
     * @return the given parts joined to the current working directory.
     */
    public static Path joinCWD(String... parts) {
        return join(getCWD(), parts);
    }

    /**
     * Returns the absolute path of the current working directory.
     * @return A {@link Path} object containing the current working directory.
     */
    public static Path getCWD() {
        return Path.of("").toAbsolutePath();
    }
}
