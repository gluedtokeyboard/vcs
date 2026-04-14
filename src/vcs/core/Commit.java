package vcs.core;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import static vcs.utils.FileSystem.join;
import static vcs.utils.FileSystem.mkdir;
import static vcs.utils.Hash.sha1;
import static vcs.utils.Repo.NULL_COMMIT;

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
     * Maps the filenames to their SHA-1 hashes. See {@link Commit#Commit(String, Date, String, String, TreeMap)} for
     * why {@link TreeMap} is used.
     */
    private final TreeMap<String, String> files;

    /**
     * The SHA-1 hash of the commit.
     */
    private final String sha1;

    /**
     * Creates a commit with no parents.
     * @param message the commit message.
     * @param timestamp the commit timestamp.
     * @param files a map mapping the filenames to their SHA-1 hashes. See
     * {@link Commit#Commit(String, Date, String, String, TreeMap)} for why {@link TreeMap} is used.
     */
    public Commit(String message, Date timestamp, TreeMap<String, String> files) {
        this(message, timestamp, NULL_COMMIT, files);
    }

    /**
     * Creates a commit with a single parent.
     * @param message the commit message.
     * @param timestamp the commit timestamp.
     * @param parent1 the SHA-1 hash of the first parent of the commit.
     * @param files a map mapping the filenames to their SHA-1 hashes. See
     * {@link Commit#Commit(String, Date, String, String, TreeMap)} for why {@link TreeMap} is used.
     */
    public Commit(String message, Date timestamp, String parent1, TreeMap<String, String> files) {
        this(message, timestamp, parent1, NULL_COMMIT, files);
    }

    /**
     * Creates a commit with two parents.
     * @param message the commit message.
     * @param timestamp the commit timestamp.
     * @param parent1 the SHA-1 hash of the first parent of the commit.
     * @param parent2 the SHA-1 hash of the second parent of the commit.
     * @param files A {@link TreeMap} mapping the filenames to their SHA-1 hashes. A {@link TreeMap} is used
     *              for a deterministic retrieval order. (The SHA-1 hash of the commit is sensitive to the file order.
     *              See {@link Commit#computeSHA1()} for more details.)
     */
     public Commit(String message, Date timestamp, String parent1, String parent2, TreeMap<String, String> files) {
        this.message = message;
        this.timestamp = timestamp;
        this.parent1 = parent1;
        this.parent2 = parent2;
        this.files = files;
        this.sha1 = this.computeSHA1();
    }

    /**
     * Computes the SHA-1 hash of the commit. The message, timestamp, parents, and the SHA-1 hashes of the files are
     * concatenated, and SHA-1 hash of the resulting string is used as the hash of the commit.
     * @return The SHA-1 hash of the commit.
     */
    private String computeSHA1() {
        List<String> strings = new LinkedList<>();
        strings.add(this.message);
        strings.add(Long.toString(this.timestamp.getTime()));
        strings.add(this.parent1);
        strings.add(this.parent2);
        strings.addAll(this.files.values());
        return sha1(strings);
    }

    /**
     * Getter for the SHA-1 hash of the commit.
     * @return The SHA-1 hash of the commit.
     */
    public String getSha1() {
        return sha1;
    }

    /**
     * Serializes the commit. See {@link Commit#getDestinationFolder(String)} for details on the destination folder.
     */
    public void serialize() {
        Path destination = getDestination(sha1);
        Path directory = destination.getParent();
        mkdir(directory);
        vcs.utils.Serialization.serialize(this, destination);
    }

    /**
     * Deserializes the commit. See {@link Commit#getDestinationFolder(String)} for details on the destination folder.
     * @param sha1 the SHA-1 hash of the commit.
     * @return the deserialized commit.
     */
    public static Commit deserialize(String sha1) {
        Path location = getDestination(sha1);
        return vcs.utils.Serialization.deserialize(location, Commit.class);
    }

    /**
     * Returns the destination for the commit.
     * @param sha1 the SHA-1 hash of the commit.
     * @return the destination for the commit.
     */
    private static Path getDestination(String sha1) {
        return join(getDestinationFolder(sha1), sha1);
    }

    /**
     * Returns the destination folder for the commit. The destination folder is given by
     * {@code <commit folder>/<first two characters of the hash>}. This makes it easier to locate the commit when
     * searching by the prefixes of its hash.
     * @param sha1 the SHA-1 hash of the commit.
     * @return the destination folder for the commit.
     */
    public static Path getDestinationFolder(String sha1) {
        return join(VCS.getCommitsFolder(), sha1.substring(0, 2));
    }

    /**
     * Returns a clone of {@link Commit#files}. This can safely be modified without affecting the original map.
     * @return a clone of {@link Commit#files}.
     */
    public TreeMap<String, String> getFilesClone() {
        return (TreeMap<String, String>) files.clone();
    }

    /**
     * Checks if the commit contains the given file.
     * @param path the path of the file.
     * @return {@code true} if the file exists within the commit; {@code false} otherwise.
     */
    public boolean contains(Path path) {
        return files.containsKey(path.toString());
    }

    /**
     * Returns the hash of the given file in the commit.
     * @param path the path of the file.
     * @return the file's hash in the commit.
     */
    public String getFileHash(Path path) {
        return files.get(path.toString());
    }

    /**
     * Getter for the commit message.
     * @return the commit message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Getter for the commit timestamp.
     * @return the commit timestamp.
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Getter for the first parent.
     * @return the SHA-1 hash of the first parent.
     */
    public String getParent1() {
        return parent1;
    }

    /**
     * Getter for the second parent.
     * @return the SHA-1 hash of the second parent
     */
    public String getParent2() {
        return parent2;
    }
}
