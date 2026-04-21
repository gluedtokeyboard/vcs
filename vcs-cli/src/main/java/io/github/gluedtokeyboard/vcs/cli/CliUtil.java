package io.github.gluedtokeyboard.vcs.cli;

import io.github.gluedtokeyboard.vcs.core.service.command.Status;
import io.github.gluedtokeyboard.vcs.core.model.Commit;

import java.util.List;
import java.util.Set;

import static io.github.gluedtokeyboard.vcs.util.FileSystem.getRelativePathCWD;

/**
 * Utils for the CLI.
 */
final class CliUtil {
    /**
     * Prints the help prompt to {@link System#err}.
     */
    static void printHelpPrompt() {
        System.err.println("Type \"vcs help\" to view usage instructions.");
    }

    /**
     * Prints the repository status.
     * @param status a {@link Status.StatusRecord} object containing the repository status.
     */
    static void printStatus(Status.StatusRecord status) {
        System.out.println("Branches:");
        for (String branch : status.branches()) {
            String prefix;
            if (branch.equals(status.currentBranch())) {
                prefix = "*";
            } else {
                prefix = " ";
            }
            System.out.println(prefix + " " + branch);
        }
        System.out.println();
        String[] categories = {
                "Added",
                "Removed",
                "Unstaged Modifications",
                "Unstaged Deletions",
                "Untracked",
        };
        Set<String>[] entries = new Set[] {
                status.stagedAdditions(),
                status.stagedDeletions(),
                status.unstagedModifications(),
                status.unstagedDeletions(),
                status.untrackedFiles(),
        };
        for (int i = 0; i < categories.length; i++) {
            System.out.println(categories[i] + ":");
            for (String file : entries[i]) {
                String relativePath = getRelativePathCWD(file);
                System.out.println(relativePath);
            }
            System.out.println();
        }
    }

    /**
     * Returns the string representation of a {@link Commit} for the CLI.
     * @param commit a {@link Commit} object.
     * @return the string representation of the given {@link Commit}.
     */
    static String commitToString(Commit commit) {
        return "commit " + commit.getSha1() + "\n"
                + "Date: " + commit.getTimestamp() + "\n"
                + "\n"
                + "    " + commit.getMessage() + "\n";
    }

    /**
     * Returns the help message.
     * @return a {@link String} containing the help message.
     */
    static String getHelp() {
        List<String> descriptions = List.of(
                "init", "Initializes a repository in the current working directory",
                "add <file>", "Stages <file> for addition",
                "rm <file>", "Stages <file> for removal",
                "commit <message>", "Creates a commit with the given message",
                "log", "Prints commits reachable from HEAD by traversing their first parents",
                "status", "Prints the status of the branches and the files",
                "checkout -- <file>", "Checks out <file> from HEAD",
                "checkout <commit id> -- <file>", "Checks out <file> from <commit id>",
                "checkout <branch>", "Checks out <branch>",
                "branch <branch>", "Creates a new branch <branch>",
                "branch -d <branch>", "Deleted the branch <branch>",
                "reset <commit id>", "Resets the state of the repository to <commit id>",
                "merge <branch>", "Merges <branch> into the current branch",
                "help", "Prints this help message"
        );
        StringBuilder sb = new StringBuilder("Usage:\n");
        for (int i = 0; i < descriptions.size(); i += 2) {
            String command = descriptions.get(i);
            String description = descriptions.get(i + 1);
            sb.append("vcs ").append(command).append("\n")
                    .append("    ").append(description).append("\n\n");
        }
        return sb.toString();
    }
}
