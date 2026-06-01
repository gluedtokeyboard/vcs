package io.github.gluedtokeyboard.vcs.api.vcs.branch.entity;

import io.github.gluedtokeyboard.vcs.api.common.idgenerator.UuidV7;
import io.github.gluedtokeyboard.vcs.api.vcs.repo.entity.Repo;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

/**
 * A repository branch.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"repo_id", "name"})
        }
)
public class Branch {
    /**
     * The branch ID.
     */
    @Id
    @UuidV7
    private UUID id;

    /**
     * The repository of the branch.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "repo_id",
            nullable = false
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Repo repo;

    /**
     * The branch name.
     */
    private String name;

    /**
     * Creates a branch with the given repository and name.
     *
     * @param repo the repository of the branch
     * @param name the branch name
     */
    public Branch(Repo repo, String name) {
        this.repo = repo;
        this.name = name;
    }
}
