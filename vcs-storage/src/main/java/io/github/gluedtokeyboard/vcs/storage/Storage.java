package io.github.gluedtokeyboard.vcs.storage;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Defines methods for interacting with the storage.
 */
public interface Storage {
    /**
     * Reads the given file into a byte array.
     * @param file the path of the file.
     * @return the contents of the file as a byte array.
     */
    byte[] read(String file);

    /**
     * Writes the given bytes to a file.
     * @param contents the contents of the file.
     * @param file the destination path.
     */
    void write(byte[] contents, String file);

    /**
     * Writes the given string to a file.
     * @param contents the contents of the file.
     * @param file the destination path.
     */
    default void write(String contents, String file) {
        byte[] bytes = contents.getBytes(StandardCharsets.UTF_8);
        write(bytes, file);
    }

    /**
     * Checks if the given path exists.
     * @param path the path of a folder or a file.
     * @return a boolean indicating whether the path exists.
     */
    boolean exists(String path);

    /**
     * Creates the given folder along with the required intermediate folders.
     * @param folder the path of the folder.
     */
    void mkdir(String folder);

    /**
     * Deletes the given path.
     * @param path the path to delete.
     * @throws IllegalStateException if the given path is a non-empty directory.
     */
    void rm(String path);

    /**
     * Lists the contents of the given directory.
     * @param folder the path of the directory.
     * @return a {@link List} containing the contents of the given directory as {@link Entry} objects.
     */
    List<Entry> ls(String folder);

    /**
     * Moves the given source to the given destination.
     * @param source the source path.
     * @param destination the destination path.
     */
    void mv(String source, String destination);

    /**
     * Returns the basename of the given path.
     * @param path a path.
     * @return the basename of the given path.
     */
    String basename(String path);

    /**
     * Walks the given directory and execute the given {@link Consumer} on files and directories.
     * @param directory the directory to walk.
     * @param excludeDirs a {@link Predicate} that returns {@code True} for the directories to skip.
     * @param consumeFiles indicates whether to pass files to the given {@link Consumer}.
     * @param consumeDirsPreVisit indicates whether to pass directories to the given {@link Consumer} before visiting
     *                           them.
     * @param consumeDirsPostVisit indicates whether to pass directories to the given {@link Consumer} after visiting
     *                            them.
     * @param consumer a {@link Consumer} to consume the files and directories.
     */
    void walk(
            String directory, Predicate<String> excludeDirs, boolean consumeFiles, boolean consumeDirsPreVisit,
            boolean consumeDirsPostVisit, Consumer<String> consumer);

    /**
     * Returns the directory of the given path.
     * @param path a path.
     * @return the directory of the given path, or {@code null} if the given path does not have a parent.
     */
    String getDirectory(String path);

    /**
     * Returns the given path relative to the given base path.
     * @param base the base path.
     * @param path a path.
     * @return the given path relative to the given base path.
     */
    String getRelativePath(String base, String path);

    /**
     * Joins the given paths.
     * @param base the base path.
     * @param paths the paths to join to the base.
     * @return the joined path.
     */
    String join(String base, String... paths);
}
