package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.jpa.entity.comparator.AnswerComparator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SortComparator;

import java.util.*;

import static jakarta.persistence.CascadeType.ALL;

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

    public static SubmissionEntity ofId(int id) {
        final var submission = new SubmissionEntity();

        submission.setId(id);

        return submission;
    }
}
