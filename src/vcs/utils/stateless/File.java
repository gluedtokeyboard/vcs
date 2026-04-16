package vcs.utils.stateless;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * File utils.
 */
public final class File {
    /**
     * Reads the file into a byte array.
     * @param path the path of the file.
     * @return the contents of the file as a byte array.
     */
    public static byte[] read(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Writes the given bytes to a file.
     * @param contents the contents of the file.
     * @param file the destination path.
     */
    public static void write(byte[] contents, Path file) {
        try {
            Files.write(file, contents);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
