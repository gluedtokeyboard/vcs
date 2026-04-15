package vcs.utils;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * File system utils.
 */
public final class FileSystem {
    /**
     * Checks if the given path exists.
     *
     * @param path The path of a folder or a file.
     * @return {@code true} if the path exists; {@code false} otherwise.
     */
    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    /**
     * Creates the given folder along with the required intermediate folders.
     *
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
     * Joins the given paths
     * @param paths the paths to join.
     * @return the joined path.
     */
    public static Path join(Path... paths) {
        Path result = paths[0];
        for (int i = 1; i < paths.length; i++) {
            result = result.resolve(paths[i]);
        }
        return result;
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
     * Deletes the given file.
     *
     * @param path the path of the file.
     */
    public static void rm(Path path) {
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lists the files in the given directory.
     * @param path the path of the directory.
     * @return the files in the given directory as stream objects.
     */
    public static Stream<Path> ls(Path path) {
        try {
            return Files.list(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Moves the given file from {@code source} to {@code destination}.
     * @param source The source path.
     * @param destination The destination path.
     */
    public static void mv(Path source, Path destination) {
        try {
            Files.move(source, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the basename of the given file.
     * @param path the path of the file.
     * @return the basename of the given file.
     */
    public static String basename(Path path) {
        return path.getFileName().toString();
    }

    /**
     * Walks the given directory and execute the given {@link Consumer} on files and directories.
     * @param directory the directory to walk.
     * @param excludedDirs the directories to skip.
     * @param consumeFiles indicates whether to pass files to the given {@link Consumer}.
     * @param consumeDirs indicates whether to pass directories to the given {@link Consumer}.
     * @param consumer a {@link Consumer} to consume the files and directories.
     */
    public static void walk(
            Path directory, Set<Path> excludedDirs, boolean consumeFiles, boolean consumeDirs,
            Consumer<Path> consumer) {
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    if (excludedDirs.contains(dir)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (consumeDirs) {
                        consumer.accept(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    if (consumeFiles) {
                        consumer.accept(file);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
    public static Path getRelativePathCWD(Path path) {
        return getRelativePath(path, getCWD());
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
