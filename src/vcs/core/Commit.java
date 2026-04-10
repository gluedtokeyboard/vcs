package vcs.core;

import java.io.Serializable;
import java.util.Date;
import java.util.TreeMap;

/**
 * {@link Commit} objects store the message, timestamp, parent(s), files, and the SHA-1 hash of the commit.
 */
public class Commit implements Serializable {
    /**
     * The commit message.
     */
    private final String message;

    /**
     * The commit timestamp.
     */
    private final Date timestamp;

    /**
     * The first parent of the commit. By default, only the first parent of commits are traversed while displaying
     * log messages.
     */
    private final String parent1;

    /**
     * The second parent of the commit. Only present if the commit is a merged commit.
     */
    private final String parent2;

    /**
     * Maps the filenames to their SHA-1 hashes.
     */
    private final TreeMap<String, String> files;

    /**
     * The SHA-1 hash of the commit.
     */
    private final String sha1;

    /**
     * Creates a commit with the given information.
     * @param message The commit message.
     * @param timestamp The commit timestamp.
     * @param parent1 The SHA-1 hash of the first parent of the commit.
     * @param parent2 The SHA-1 hash of the second parent of the commit.
     * @param files A map mapping the filenames to their SHA-1 hashes.
     */
     public Commit(String message, Date timestamp, String parent1, String parent2, TreeMap<String, String> files) {
        this.message = message;
        this.timestamp = timestamp;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.files = files;
    }
}
