package vcs.command;

import vcs.core.Blob;
import vcs.core.Commit;
import vcs.core.VCS;
import vcs.util.stateful.StagingArea;

import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static vcs.util.stateful.General.walkRepoFiles;
import static vcs.util.stateless.General.setDifference;

/**
 * Methods for getting the status of the repository.
 */
public class Status extends Command {
    /**
     * Stores the current repository.
     *
     * @param vcs the current repository object.
     */
    public Status(VCS vcs) {
        super(vcs);
    }

    /**
     * The status is composed of the following:
     * <ul>
     *     <li>A list of branches</li>
     *     <li>The current branch</li>
     *     <li>The files staged for addition</li>
     *     <li>The files staged for deletion</li>
     *     <li>Unstaged modifications</li>
     *     <li>Unstaged deletions</li>
     *     <li>Untracked files</li>
     * </ul>
     * @return A {@link StatusRecord} object containing those items.
     */
    public StatusRecord status() {
        List<String> branches = vcs.getAllBranches().toList();
        String currentBranch = vcs.getCurrentBranch();
        TreeMap<String, String> stagedFiles = vcs.getStagedFilesClone();
        Set<String> stagedAdditions = getStagedAdditions(stagedFiles);
        Set<String> stagedDeletions = getStagedDeletions(stagedFiles);
        Set<String> unstagedModifications = new HashSet<>();
        Set<String> untrackedFiles = new HashSet<>();
        Set<String> cwdFiles = new HashSet<>();
        Commit latestCommit = commitUtil.getLatestCommitCurrentBranch();
        Set<String> latestCommitFiles = getCommitFiles(latestCommit);
        walkRepoFiles(file -> {
            String fileString = file.toString();
            if (isUnstagedMod(latestCommit, latestCommitFiles, file)) {
                unstagedModifications.add(fileString);
            } else if (isUntracked(stagedDeletions, latestCommitFiles, file)) {
                untrackedFiles.add(fileString);
            }
            cwdFiles.add(fileString);
        });
        Set<String> unstagedDeletions = new HashSet<>();
        unstagedDeletions.addAll(setDifference(stagedAdditions, cwdFiles));
        unstagedDeletions.addAll(setDifference(latestCommitFiles, cwdFiles, stagedDeletions));
        return new StatusRecord(
                branches, currentBranch, stagedAdditions, stagedDeletions, unstagedModifications, unstagedDeletions,
                untrackedFiles
        );
    }

    /**
     * Returns the files tracked by the given commit.
     * @param commit a {@link Commit} object.
     * @return the {@link Set} of files tracked by the given commit.
     */
    private Set<String> getCommitFiles(Commit commit) {
        if (commit == null) {
            return Collections.emptySet();
        }
        return commit.getCommitFiles().collect(Collectors.toSet());
    }

    /**
     * Checks if the given file has been modified but hasn't been staged for addition.
     * @param latestCommit the latest {@link Commit} in the current branch.
     * @param latestCommitFiles the files tracked by the latest {@link Commit} in the current branch.
     * @param file the path of the file.
     * @return a boolean indicating whether the given file has been modified but not staged for addition.
     */
    private boolean isUnstagedMod(Commit latestCommit, Set<String> latestCommitFiles, Path file) {
        String fileString = file.toString();
        Blob blob = new Blob(file);
        return latestCommitFiles.contains(fileString)
                && !latestCommit.getFileHash(file).equals(blob.getSha1())
                && !vcs.isStaged(file)
                || (vcs.isStaged(file) && !vcs.getStagedSha(file).equals(blob.getSha1()));
    }

    /**
     * Checks if the given file is untracked.
     * @param stagedDeletions the {@link Set} of files currently staged for deletion.
     * @param latestCommitFiles the files tracked by the latest {@link Commit} in the current branch.
     * @param file the path of the file.
     * @return a boolean indicating whether the given file is untracked.
     */
    private boolean isUntracked(Set<String> stagedDeletions, Set<String> latestCommitFiles, Path file) {
        String fileString = file.toString();
        return stagedDeletions.contains(fileString)
                || (!latestCommitFiles.contains(fileString) && !vcs.isStaged(file));
    }

    /**
     * Returns the files currently staged for addition.
     * @param stagedFiles all the files currently staged for addition or deletion.
     * @return a {@link Set} containing the files currently staged for addition.
     */
    private Set<String> getStagedAdditions(TreeMap<String, String> stagedFiles) {
        return filterStagedFiles(
                stagedFiles,
                entry -> !entry.getValue().equals(StagingArea.DELETION_MARKER)
        ).collect(Collectors.toSet());
    }

    /**
     * Returns the files currently staged for deletion.
     * @param stagedFiles all the files currently staged for addition or deletion.
     * @return a {@link Set} containing the files currently staged for deletion.
     */
    private Set<String> getStagedDeletions(TreeMap<String, String> stagedFiles) {
        return filterStagedFiles(
                stagedFiles,
                entry -> entry.getValue().equals(StagingArea.DELETION_MARKER)
        ).collect(Collectors.toSet());
    }

    /**
     * Filters the staged files.
     * @param filter a {@link Predicate} to filter the staged files.
     * @return a {@link Stream} containing the remaining files.
     */
    private Stream<String> filterStagedFiles(
            TreeMap<String, String> stagedFiles, Predicate<Map.Entry<String, String>> filter) {
        return stagedFiles
                .entrySet()
                .stream()
                .filter(filter)
                .map(Map.Entry::getKey);
    }

    /**
     * Stores all the components of the status.
     * @param branches a list of branches.
     * @param currentBranch the current branch.
     * @param stagedAdditions the files staged for addition.
     * @param stagedDeletions the files staged for deletion.
     * @param unstagedModifications the files that have been modified but haven't been staged for addition.
     * @param unstagedDeletions the files that have been removed but haven't been staged for deletion.
     * @param untrackedFiles the untracked files.
     */
    public record StatusRecord(
            List<String> branches, String currentBranch, Set<String> stagedAdditions, Set<String> stagedDeletions,
            Set<String> unstagedModifications, Set<String> unstagedDeletions, Set<String> untrackedFiles) {}
}
