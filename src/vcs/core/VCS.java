package vcs.core;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * The {@link VCS} class stores all metadata about the repository.
 */
public class VCS implements Serializable {
    /**
     * The folder used to store the repository data and metadata.
     */
    private static final String VCS_FOLDER = ".vcs";

    /**
     * The staging area folder. This will contain {@link Blob} objects of the staged files.
     */
    private static final String STAGING_AREA = "index";

    /**
     * The folder containing {@link Commit} objects.
     */
    private static final String COMMITS_FOLDER = "commits";

    /**
     * The folder containing {@link Blob} objects of the committed files.
     */
    private static final String BLOBS_FOLDER = "blobs";

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
    public VCS() {
        branchMap.put("main", null);
    }
}
