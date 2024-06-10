package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.jpa.entity.comparator.AnswerComparator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.SortComparator;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.util.*;

import static javax.persistence.CascadeType.ALL;

@Getter
@Setter
@Entity
@Table(name = "submission")
public class SubmissionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    private MarathonEntity marathon;

    @OneToMany(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private Set<GameEntity> games;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "availability", joinColumns = @JoinColumn(name = "submission_id"))
    @AttributeOverrides({
        @AttributeOverride(name = "from", column = @Column(name = "date_from")),
        @AttributeOverride(name = "to", column = @Column(name = "date_to"))
    })
    @OrderBy(value = "date_from ASC")
    private List<AvailabilityEntity> availabilities;

    @OneToMany(mappedBy = "submission", cascade = ALL, orphanRemoval = true)
    @SortComparator(AnswerComparator.class)
    private SortedSet<AnswerEntity> answers;

    @OneToMany(mappedBy = "submission", cascade = ALL, orphanRemoval = true)
    private Set<OpponentEntity> opponents = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubmissionEntity that = (SubmissionEntity) o;
        return id == that.id && user.equals(that.user) && marathon.equals(that.marathon) && games.equals(that.games);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, marathon, games, opponents);
    }

    @Deprecated(forRemoval = true)
    public SubmissionEntity fresh(boolean withGames) {
        final SubmissionEntity submission = new SubmissionEntity();

        // load all the items needed from the old submission
        Hibernate.initialize(this.getAvailabilities());
        Hibernate.initialize(this.getOpponents());
        Hibernate.initialize(this.getAnswers());

        // the games will be copied separately
        BeanUtils.copyProperties(this, submission, "games");

        // only load the game if we say so
        // might cause issues if we load the submission from a game otherwise
        if (withGames) {
            final Set<GameEntity> freshGames = new HashSet<>();

            this.getGames().forEach(
                (game) -> freshGames.add(game.fresh(false))
            );

            submission.setGames(freshGames);
        }

        return submission;
    }

    @Deprecated(forRemoval = true)
    public static void initialize(SubmissionEntity submission, boolean withGames) {
        // load all the items needed from the old submission
        Hibernate.initialize(submission.getAvailabilities());
        Hibernate.initialize(submission.getOpponents());
        Hibernate.initialize(submission.getAnswers());

        // only load the game if we say so
        // might cause issues if we load the submission from a game otherwise
        if (withGames) {
            submission.getGames().forEach(GameEntity::initialize);
        }
    }

    public static SubmissionEntity ofId(int id) {
        final var submission = new SubmissionEntity();

        submission.setId(id);

        return submission;
    }
}
