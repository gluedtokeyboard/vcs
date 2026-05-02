package io.github.gluedtokeyboard.vcs.core.model;

import io.github.gluedtokeyboard.vcs.storage.Storage;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static io.github.gluedtokeyboard.vcs.util.Hash.sha1;

/**
 * Stores a file as a byte array.
 */
public class Blob implements Serializable {
    /**
     * The object used to interact with the storage.
     */
    private final Storage storage;

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
     * @param storage a {@link Storage} implementation.
     * @param path the path of the file.
     */
    public Blob(Storage storage, String path) {
        this.storage = storage;
        this.contents = storage.read(path);
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
    public void writeToFile(String file) {
        storage.write(contents, file);
    }

    /**
     * Getter for the blob contents.
     * @return the contents of the blob as a string.
     */
    public String getContents() {
        return new String(contents, StandardCharsets.UTF_8);
    }
}
