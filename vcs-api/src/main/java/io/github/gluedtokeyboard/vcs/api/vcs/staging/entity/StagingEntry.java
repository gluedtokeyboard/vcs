package io.github.gluedtokeyboard.vcs.api.vcs.staging.entity;

import io.github.gluedtokeyboard.vcs.api.common.idgenerator.UuidV7;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import io.github.gluedtokeyboard.vcs.api.vcs.staging.model.EntryType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

/**
 * A staging area entry.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"repo_id", "relativePath"})
        }
)
public class StagingEntry {
    /**
     * The ID of the entry.
     */
    @Id
    @UuidV7
    private UUID id;

    /**
     * The repository of the entry.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "repo_id",
            nullable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Repo repo;

    /**
     * The repository relative path of the file.
     */
    private String relativePath;

    /**
     * The entry type.
     */
    @Enumerated(EnumType.STRING)
    private EntryType type;

    /**
     * Creates a staging entry with the given repository, relative path, and type.
     *
     * @param repo the repository of the entry
     * @param relativePath the repository relative path of the file
     * @param type the entry type
     */
    public StagingEntry(Repo repo, String relativePath, EntryType type) {
        this.repo = repo;
        this.relativePath = relativePath;
        this.type = type;
    }
}
