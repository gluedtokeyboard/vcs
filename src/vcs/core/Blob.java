package vcs.core;

import java.io.Serializable;
import java.nio.file.Path;

import static vcs.utils.File.read;
import static vcs.utils.File.write;
import static vcs.utils.Hash.sha1;

/**
 * Stores a file as a byte array.
 */
public class Blob implements Serializable {
    /**
     * The contents of the file.
     */
    private final byte[] contents;

    /**
     * The SHA-1 hash of the contents.
     */
    private final String sha1;

    /**
     * Creates a blob of the given file.
     * @param path the path of the file.
     */
    public Blob(Path path) {
        this.contents = read(path);
        this.sha1 = sha1(this.contents);
    }

    /**
     * Getter for the SHA-1 hash of the blob.
     * @return The SHA-1 hash of the blob.
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * Writes the contents of the blob to a file.
     * @param file the path of the file.
     */
    public void writeToFile(Path file) {
        write(contents, file);
    }
}
