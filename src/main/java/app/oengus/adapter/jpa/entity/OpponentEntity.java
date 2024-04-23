package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

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
}
