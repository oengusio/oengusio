package app.oengus.adapter.jpa.entity;

import app.oengus.domain.submission.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "game")
public class GameEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private SubmissionEntity submission;

    @Column(name = "name")
    @NotBlank
    @Size(max = Game.NAME_MAX_LENGTH)
    private String name;

    @Column(name = "description")
    @NotBlank
    @Size(max = Game.DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(name = "console")
    @NotBlank
    @Size(max = Game.CONSOLE_MAX_LENGTH) // can grow upto 100 due to db type being varchar(100)
    private String console;

    @Column(name = "ratio")
    @NotBlank
    @Size(max = Game.RATIO_MAX_LENGTH)
    private String ratio;

    @Column(name = "emulated")
    private boolean emulated;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<CategoryEntity> categories;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameEntity game = (GameEntity) o;
        return emulated == game.emulated && Objects.equals(id, game.id) && Objects.equals(name, game.name) && Objects.equals(description, game.description) && Objects.equals(console, game.console) && Objects.equals(ratio, game.ratio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, console, ratio, emulated, categories);
    }

    public static GameEntity ofId(int id) {
        final var game = new GameEntity();

        game.setId(id);

        return game;
    }
}
