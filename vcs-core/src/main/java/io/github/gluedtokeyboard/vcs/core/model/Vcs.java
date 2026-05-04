package io.github.gluedtokeyboard.vcs.core.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * The {@link Vcs} class stores all metadata about the repository.
 */
public class Vcs implements Serializable {
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

    /**
     * Stages the given file.
     * @param repoRelativePath the path of the file relative to the repository root.
     * @param sha1 the SHA-1 hash of the file.
     */
    public void stageFile(String repoRelativePath, String sha1) {
        stagedFiles.put(repoRelativePath, sha1);
    }

    /**
     * Unstages the given file.
     * @param repoRelativePath the path of the file relative to the repository root.
     */
    public void unstageFile(String repoRelativePath) {
        stagedFiles.remove(repoRelativePath);
    }

    /**
     * Checks if the given file is currently stages.
     * @param repoRelativePath the path of the file relative to the repository root.
     * @return {@code true} if the file is staged; {@code false} otherwise.
     */
    public boolean isStaged(String repoRelativePath) {
        return stagedFiles.containsKey(repoRelativePath);
    }

    /**
     * Returns the SHA-1 hash of the staged file.
     * @param repoRelativePath the path of the file relative to the repository root.
     * @return the SHA-1 hash of the staged file.
     */
    public String getStagedSha(String repoRelativePath) {
        return stagedFiles.get(repoRelativePath);
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
