package io.github.gluedtokeyboard.vcs.cli;

import io.github.gluedtokeyboard.vcs.core.context.VcsContext;
import io.github.gluedtokeyboard.vcs.core.model.Commit;
import io.github.gluedtokeyboard.vcs.core.service.command.*;

import java.util.ArrayList;

import static io.github.gluedtokeyboard.vcs.cli.CliConfig.*;
import static io.github.gluedtokeyboard.vcs.cli.CliUtil.*;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.getCWD;
import static io.github.gluedtokeyboard.vcs.util.FileSystem.joinCWD;

/**
 * The entry point for the CLI.
 */
public class CliApplication {
    /**
     * Parses the given arguments and calls the appropriate command.
     * @param args the command-line arguments for the application.
     */
    static void main(String[] args) {
        String cwd = getCWD();
        try {
            String command = args[0];
            if (command.equals("init")) {
                init().init(cwd);
            } else {
                VcsContext ctx = VcsContext.getContext(storage(), vcsStore(), cwd);
                String absolutePath;
                switch (command) {
                    case "add":
                        absolutePath = joinCWD(args[1]);
                        add().add(ctx, absolutePath);
                        break;
                    case "commit":
                        String message;
                        if (args.length < 2) {
                            message = null;
                        } else {
                            message = args[1];
                        }
                        commitCommand().commit(ctx, message);
                        break;
                    case "rm":
                        absolutePath = joinCWD(args[1]);
                        rm().rm(ctx, absolutePath);
                        break;
                    case "log":
                        ArrayList<Commit> commits = log().log(ctx);
                        commits.stream().map(CliUtil::commitToString).forEach(System.out::println);
                        break;
                    case "status":
                        Status.StatusRecord status = status().status(ctx);
                        printStatus(status);
                        break;
                    case "branch":
                        Branch branch = branch();
                        if (args[1].equals("-d")) {
                            branch.removeBranch(ctx, args[2]);
                        } else {
                            branch.createBranch(ctx, args[1]);
                        }
                        break;
                    case "checkout":
                        Checkout checkout = checkout();
                        switch (args.length) {
                            case 2:
                                checkout.checkoutBranch(ctx, args[1]);
                                break;
                            case 3:
                                absolutePath = joinCWD(args[2]);
                                checkout.checkout(ctx, absolutePath);
                                break;
                            case 4:
                                absolutePath = joinCWD(args[3]);
                                checkout.checkout(ctx, args[1], absolutePath);
                                break;
                        }
                        break;
                    case "reset":
                        reset().reset(ctx, args[1]);
                        break;
                    case "merge":
                        merge().merge(ctx, args[1]);
                        break;
                    case "help":
                        System.out.print(getHelp());
                        break;
                    default:
                        System.err.println("Unknown command: " + command);
                        printHelpPrompt();
                        break;
                }
                vcsStore().store(ctx.repoRoot(), ctx.vcs());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Insufficient arguments.");
            printHelpPrompt();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
        }
    }
}
