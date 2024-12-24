package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "opponent")
public class OpponentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private CategoryEntity category;

    @ManyToOne
    @JoinColumn(name = "opponent_submission_id")
    private SubmissionEntity submission;

    @Column(name = "video")
    @Size(max = 100)
    private String video;

    public static OpponentEntity ofId(int id) {
        final var opponent = new OpponentEntity();

        opponent.setId(id);

        return opponent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof OpponentEntity e) {
            return Objects.equals(getId(), e.getId()) &&
                Objects.equals(getVideo(), e.getVideo());
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, video);
    }
}
