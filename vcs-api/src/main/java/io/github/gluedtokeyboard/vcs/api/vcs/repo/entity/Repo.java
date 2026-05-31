package io.github.gluedtokeyboard.vcs.api.vcs.repo.entity;

import io.github.gluedtokeyboard.vcs.api.common.idgenerator.UuidV7;
import io.github.gluedtokeyboard.vcs.api.filesystem.common.entity.FilesystemNode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

/**
 * A VCS repository.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Repo {
    /**
     * The repository ID.
     */
    @Id
    @UuidV7
    private UUID id;

    /**
     * The repository root.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "repo_root_id",
            nullable = false,
            unique = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FilesystemNode repoRoot;

    /**
     * The repository folder.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "repo_folder",
            nullable = false,
            unique = true
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FilesystemNode repoFolder;

    /**
     * Creates a repository using the given repository root and repository folder.
     *
     * @param repoRoot the repository root
     * @param repoFolder the repository folder
     */
    public Repo(FilesystemNode repoRoot, FilesystemNode repoFolder) {
        this.repoRoot = repoRoot;
        this.repoFolder = repoFolder;
    }
}
