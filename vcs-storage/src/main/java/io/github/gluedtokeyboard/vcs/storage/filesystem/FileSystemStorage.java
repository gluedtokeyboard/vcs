package io.github.gluedtokeyboard.vcs.storage.filesystem;

import io.github.gluedtokeyboard.vcs.storage.Entry;
import io.github.gluedtokeyboard.vcs.storage.EntryType;
import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A filesystem implementation of storage.
 */
public class FileSystemStorage implements Storage {
    /**
     * Defines the root of the file system. Can be any directory.
     */
    private final Path root;

    /**
     * Stores the root of the file system.
     * @param root the path of the root directory.
     */
    public FileSystemStorage(String root) {
        this.root = Path.of(root);
    }

    /**
     * Reads the given file into a byte array.
     *
     * @param file the path of the file.
     * @return the contents of the file as a byte array.
     */
    @Override
    public byte[] read(String file) {
        Path realPath = getRealPath(file);
        try {
            return Files.readAllBytes(realPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the given bytes to a file.
     *
     * @param contents the contents of the file.
     * @param file     the destination path.
     */
    @Override
    public void write(byte[] contents, String file) {
        Path realPath = getRealPath(file);
        Path parent = realPath.getParent();
        try {
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(realPath, contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the given path exists.
     *
     * @param path the path of a folder or a file.
     * @return a boolean indicating whether the path exists.
     */
    @Override
    public boolean exists(String path) {
        return Files.exists(getRealPath(path));
    }

    /**
     * Creates the given folder along with the required intermediate folders.
     *
     * @param folder the path of the folder.
     */
    @Override
    public void mkdir(String folder) {
        Path realPath = getRealPath(folder);
        try {
            Files.createDirectories(realPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes the given file.
     *
     * @param path the path to delete.
     * @throws IllegalStateException if the given path is a non-empty directory.
     */
    @Override
    public void rm(String path) {
        Path realPath = getRealPath(path);
        try {
            if (Files.isDirectory(realPath)) {
                walk(path, (_) -> false, true, false, true, (path_) -> {
                    try {
                        Path realPath_ = getRealPath(path_);
                        Files.delete(realPath_);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            } else {
                Files.delete(realPath);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lists the contents of the given directory.
     *
     * @param folder the path of the directory.
     * @return a {@link List} containing the contents of the given directory as {@link Entry} objects.
     */
    @Override
    public List<Entry> ls(String folder) {
        Path realPath = getRealPath(folder);
        try (Stream<Path> files = Files.list(realPath)) {
            return files.map(path -> {
                String fileSystemPath = getFileSystemPath(path);
                EntryType type = Files.isRegularFile(path) ? EntryType.FILE : EntryType.DIRECTORY;
                return new Entry(fileSystemPath, type);
            }).toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Moves the given source to the given destination.
     *
     * @param source      the source path.
     * @param destination the destination path.
     */
    @Override
    public void mv(String source, String destination) {
        Path realSource = getRealPath(source);
        Path realDestination = getRealPath(destination);
        try {
            Files.move(realSource, realDestination);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Copies the given source to the given destination.
     *
     * @param source the source path
     * @param destination the destination path
     */
    @Override
    public void cp(String source, String destination) {
        Path realDestination = getRealPath(destination);
        walk(source, _ -> false, true, true, false, source_ -> {
            Path realSource_ = getRealPath(source_);
            String relativePath = getRelativePath(source, source_);
            Path realDestination_ = Path.of(join(realDestination.toString(), relativePath));
            try {
                if (Files.isDirectory(realSource_)) {
                    Files.createDirectories(realDestination_);
                } else {
                    Files.copy(realSource_, realDestination_, StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Returns the basename of the given path.
     *
     * @param path a path.
     * @return the basename of the given path.
     */
    @Override
    public String basename(String path) {
        return Path.of(path).getFileName().toString();
    }

    /**
     * Walks the given directory and execute the given {@link Consumer} on files and directories.
     *
     * @param directory            the directory to walk.
     * @param excludeDirs          a {@link Predicate} that returns {@code True} for the directories to skip.
     * @param consumeFiles         indicates whether to pass files to the given {@link Consumer}.
     * @param consumeDirsPreVisit  indicates whether to pass directories to the given {@link Consumer} before visiting
     *                             them.
     * @param consumeDirsPostVisit indicates whether to pass directories to the given {@link Consumer} after visiting
     *                             them.
     * @param consumer             a {@link Consumer} to consume the files and directories.
     */
    @Override
    public void walk(
            String directory, Predicate<String> excludeDirs, boolean consumeFiles, boolean consumeDirsPreVisit,
            boolean consumeDirsPostVisit, Consumer<String> consumer) {
        Path realDirectory = getRealPath(directory);
        try {
            Files.walkFileTree(realDirectory, new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    String dirFileSystem = getFileSystemPath(dir);
                    if (excludeDirs.test(dirFileSystem)) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    if (consumeDirsPreVisit) {
                        consumer.accept(dirFileSystem);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                    String dirFileSystem = getFileSystemPath(dir);
                    if (consumeDirsPostVisit) {
                        consumer.accept(dirFileSystem);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                    String fileFileSystem = getFileSystemPath(file);
                    if (consumeFiles) {
                        consumer.accept(fileFileSystem);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the directory of the given path.
     *
     * @param path a path.
     * @return the directory of the given path, or {@code null} if the given path does not have a parent.
     */
    @Override
    public String getDirectory(String path) {
        Path realPath = getRealPath(path);
        if (realPath.equals(root)) {
            return null;
        }
        Path parent = realPath.getParent();
        return getFileSystemPath(parent);
    }

    /**
     * Returns the given path relative to the given base path.
     *
     * @param base the base path.
     * @param path a path.
     * @return the given path relative to the given base path.
     */
    @Override
    public String getRelativePath(String base, String path) {
        return getRelativePath(Path.of(base), Path.of(path));
    }

    /**
     * Joins the given paths.
     *
     * @param base  the base path.
     * @param paths the paths to join to the base.
     * @return the joined path.
     */
    @Override
    public String join(String base, String... paths) {
        Path joined = Path.of(base);
        for (String path : paths) {
            joined = joined.resolve(path);
        }
        String joinedString = joined.toString();
        return convertToUnixPath(joinedString);
    }

    /**
     * Returns the given path's real path on the filesystem.
     * @param path a path
     * @return the given path's real path on the filesystem.
     */
    private Path getRealPath(String path) {
        if (path.startsWith("/")) {
            // Remove the leading path separator.
            path = path.substring(1);
        }
        String pathString = join(root.toString(), path);
        return Path.of(pathString);
    }

    /**
     * Relativizes the given path with respect to the current object's root.
     * @param path a real absolute path.
     * @return the given path relative to the current object's root.
     */
    private String getFileSystemPath(Path path) {
        return "/" + getRelativePath(root, path);
    }

    /**
     * Relativizes the given path with respect to the given base path.
     * @param base the base path.
     * @param path a path.
     * @return the given path relative to the given base path.
     */
    private static String getRelativePath(Path base, Path path) {
        String relativePath = base.relativize(path).toString();
        return convertToUnixPath(relativePath);
    }

    /**
     * Converts the given path to a Unix-style path.
     * @param path a path.
     * @return the given path converted to a Unix-style path.
     */
    private static String convertToUnixPath(String path) {
        return path.replace("\\", "/");
    }
}
