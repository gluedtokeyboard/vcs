package vcs.cli;

import vcs.command.*;
import vcs.core.model.Commit;
import vcs.core.model.VCS;

import java.util.ArrayList;

import static vcs.cli.CliUtil.*;

/**
 * The entry point for the CLI.
 */
public class CliApplication {
    /**
     * Parses the given arguments and calls the appropriate command.
     * @param args the command-line arguments for the application.
     */
    static void main(String[] args) {
        VCS vcs;
        try {
            String command = args[0];
            if (command.equals("init")) {
                vcs = new VCS();
                new Init(vcs).init();
            } else {
                vcs = VCS.deserialize();
                switch (command) {
                    case "add":
                        new Add(vcs).add(args[1]);
                        break;
                    case "commit":
                        String message;
                        if (args.length < 2) {
                            message = null;
                        } else {
                            message = args[1];
                        }
                        new CommitCommand(vcs).commit(message);
                        break;
                    case "rm":
                        new Rm(vcs).rm(args[1]);
                        break;
                    case "log":
                        ArrayList<Commit> commits = new Log(vcs).log();
                        commits.stream().map(CliUtil::commitToString).forEach(System.out::println);
                        break;
                    case "status":
                        Status.StatusRecord status = new Status(vcs).status();
                        printStatus(status);
                        break;
                    case "branch":
                        Branch branch = new Branch(vcs);
                        if (args[1].equals("-d")) {
                            branch.removeBranch(args[2]);
                        } else {
                            branch.createBranch(args[1]);
                        }
                        break;
                    case "checkout":
                        Checkout checkout = new Checkout(vcs);
                        switch (args.length) {
                            case 2:
                                checkout.checkoutBranch(args[1]);
                                break;
                            case 3:
                                checkout.checkout(args[2]);
                                break;
                            case 4:
                                checkout.checkout(args[1], args[3]);
                                break;
                        }
                        break;
                    case "reset":
                        new Reset(vcs).reset(args[1]);
                        break;
                    case "merge":
                        new Merge(vcs).merge(args[1]);
                        break;
                    case "help":
                        System.out.print(getHelp());
                        break;
                    default:
                        System.err.println("Unknown command: " + command);
                        printHelpPrompt();
                        break;
                }
            }
            vcs.serialize();
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Insufficient arguments.");
            printHelpPrompt();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }
}
