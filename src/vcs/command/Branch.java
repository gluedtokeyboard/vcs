package vcs.command;

import vcs.core.VCS;

/**
 * Methods to create/remove branches.
 */
public class Branch extends Command {
    /**
     * Stores the current repository, and initializes the repository utils object with it.
     *
     * @param vcs the current repository object.
     */
    public Branch(VCS vcs) {
        super(vcs);
    }

    /**
     * Creates a new branch.
     * @param branchName the name of the branch.
     * @throws RuntimeException if the branch already exists.
     */
    public void createBranch(String branchName) {
        if (vcs.containsBranch(branchName)) {
            throw new RuntimeException("Branch " + branchName + " already exists");
        }
        vcs.setBranchHead(branchName, vcs.getCurrentBranchHead());
    }

    /**
     * Removes an existing branch.
     * @param branchName the name of the branch.
     * @throws RuntimeException if the branch does not exist, or if it is the current branch.
     */
    public void removeBranch(String branchName) {
        if (!vcs.containsBranch(branchName)) {
            throw new RuntimeException("Branch " + branchName + " does not exist");
        }
        if (branchName.equals(vcs.getCurrentBranch())) {
            throw new RuntimeException("Cannot remove the current branch");
        }
        vcs.removeBranch(branchName);
    }
}
