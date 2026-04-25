package io.github.gluedtokeyboard.vcs.core.model;

import io.github.gluedtokeyboard.vcs.util.Serialization;

import java.io.Serializable;
import java.nio.file.Path;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.join;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.joinCWD;

/**
 * The {@link Vcs} class stores all metadata about the repository.
 */
public class Vcs implements Serializable {
    /**
     * The folder used to store the repository data and metadata.
     */
    private static final String REPO_FOLDER = ".vcs";

    /**
     * The staging area folder. This will contain {@link Blob} objects of the staged files.
     */
    private static final String STAGING_AREA_FOLDER = "index";

    /**
     * The folder containing {@link Commit} objects.
     */
    private static final String COMMITS_FOLDER = "commits";

    /**
     * The folder containing {@link Blob} objects of the committed files.
     */
    private static final String BLOBS_FOLDER = "blobs";

    /**
     * The name to use for serialization/deserialization.
     */
    private static final String OBJECT_NAME = "vcs";

    /**
     * The name of the current branch.
     */
    private String currentBranch = "main";

    /**
     * Maps the branch name to the SHA-1 hash of the branch head.
     */
    private final Map<String, String> branchMap = new HashMap<>();

    /**
     * Maps the staged file names to their SHA-1 hashes. See
     * {@link Commit#Commit(String, Date, String, String, TreeMap)} for why {@link TreeMap} is used.
     */
    private final TreeMap<String, String> stagedFiles = new TreeMap<>();

    /**
     * Initializes the repository object with a default branch named "main".
     */
    public Vcs() {
        branchMap.put("main", null);
    }

    /*
     * Resolves the given path relative to the repository folder.
     * @param path the relative path.
     * @return the absolute path obtained by resolving the given path against the repository folder.
     */
    private static Path resolvePath(String path) {
        return join(getRepoFolder(), path);
    }

    /**
     * Serializes the current object and writes it to {@link Vcs#REPO_FOLDER}/{@link Vcs#OBJECT_NAME}.
     */
    public void serialize() {
        Path path = resolvePath(OBJECT_NAME);
        Serialization.serialize(this, path);
    }

    /**
     * Deserializes the {@link Vcs} object from {@link Vcs#REPO_FOLDER}/{@link Vcs#OBJECT_NAME}.
     * @return The deserialized {@link Vcs} object.
     */
    public static Vcs deserialize() {
        Path path = resolvePath(OBJECT_NAME);
        return Serialization.deserialize(path, Vcs.class);
    }

    /**
     * Getter for the repository folder.
     * @return The absolute path of the repository folder.
     */
    public static Path getRepoFolder() {
        return joinCWD(REPO_FOLDER);
    }

    /**
     * Getter for the staging area folder.
     * @return The absolute path of the staging area folder.
     */
    public static Path getStagingAreaFolder() {
        return resolvePath(STAGING_AREA_FOLDER);
    }

    /**
     * Getter for the commits folder.
     * @return The absolute path of the commits folder.
     */
    public static Path getCommitsFolder() {
        return resolvePath(COMMITS_FOLDER);
    }

    /**
     * Getter for the blobs folder.
     * @return The absolute path of the blobs folder.
     */
    public static Path getBlobsFolder() {
        return resolvePath(BLOBS_FOLDER);
    }

    /**
     * Stages the given file.
     * @param path the path of the file.
     * @param sha1 the SHA-1 hash of the file.
     */
    public void stageFile(Path path, String sha1) {
        stagedFiles.put(path.toString(), sha1);
    }

    /**
     * Unstages the given file.
     * @param path the path of the file.
     */
    public void unstageFile(Path path) {
        stagedFiles.remove(path.toString());
    }

    /**
     * Checks if the given file is currently stages.
     * @param path the path of the file.
     * @return {@code true} if the file is staged; {@code false} otherwise.
     */
    public boolean isStaged(Path path) {
        return stagedFiles.containsKey(path.toString());
    }

    /**
     * Returns the SHA-1 hash of the staged file.
     * @param path the path of the staged file.
     * @return the SHA-1 hash of the staged file.
     */
    public String getStagedSha(Path path) {
        return stagedFiles.get(path.toString());
    }

    /**
     * Checks if the staging area is empty.
     * @return {@code true} if the staging area is empty; {@code false} otherwise.
     */
    public boolean stagingAreaIsEmpty() {
        return stagedFiles.isEmpty();
    }

    /**
     * Clears the staging area.
     */
    public void clearStagingArea() {
        stagedFiles.clear();
    }

    /**
     * Getter for the current branch.
     * @return the current branch.
     */
    public String getCurrentBranch() {
        return currentBranch;
    }

    /**
     * Setter for the current branch.
     * @param branch the new branch.
     */
    public void setCurrentBranch(String branch) {
        currentBranch = branch;
    }

    /**
     * Returns the branch head of the given branch.
     * @param branch the branch name.
     * @return the SHA-1 hash of the branch head of the given branch.
     */
    public String getBranchHead(String branch) {
        return branchMap.get(branch);
    }

    /**
     * Returns the SHA-1 hash of the branch head of the current branch.
     * @return the SHA-1 hash of the branch head of the current branch.
     */
    public String getCurrentBranchHead() {
        return getBranchHead(getCurrentBranch());
    }

    /**
     * Sets the branch head for the given branch.
     * @param branch the name of the branch.
     * @param head the SHA-1 hash of the new head.
     */
    public void setBranchHead(String branch, String head) {
        branchMap.put(branch, head);
    }

    /**
     * Sets the branch head for the current branch.
     * @param head the SHA-1 hash of the new head.
     */
    public void setCurrentBranchHead(String head) {
        setBranchHead(getCurrentBranch(), head);
    }

    /**
     * Returns all the branches.
     * @return a stream over all the branches.
     */
    public Stream<String> getAllBranches() {
        return branchMap.keySet().stream();
    }

    /**
     * Checks if the given branch exists.
     * @param branch the branch name.
     * @return a boolean indicating if the given branch exists.
     */
    public boolean containsBranch(String branch) {
        return branchMap.containsKey(branch);
    }

    /**
     * Removes the given branch.
     * @param branch the name of the branch to remove.
     */
    public void removeBranch(String branch) {
        branchMap.remove(branch);
    }

    /**
     * Returns a clone of {@link Vcs#stagedFiles}. This can safely be passed to other classes without affecting the
     * original map.
     * @return a clone of {@link Vcs#stagedFiles}.
     */
    public TreeMap<String, String> getStagedFilesClone() {
        return (TreeMap<String, String>) stagedFiles.clone();
    }
}
