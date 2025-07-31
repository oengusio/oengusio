package app.oengus.adapter.jpa.entity;

import app.oengus.domain.submission.Game;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@EqualsAndHashCode(exclude = { "user", "categories" })
@Table(name = "saved_games")
public class SavedGameEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @NotBlank
    @Column(name = "name")
    @Size(max = Game.NAME_MAX_LENGTH)
    private String name;

    @NotBlank
    @Column(name = "description")
    @Size(max = Game.DESCRIPTION_MAX_LENGTH)
    private String description;

    @NotBlank
    @Column(name = "console")
    @Size(max = Game.CONSOLE_MAX_LENGTH)
    private String console;

    @NotBlank
    @Column(name = "ratio")
    @Size(max = Game.RATIO_MAX_LENGTH)
    private String ratio;

    @Column(name = "emulated")
    private boolean emulated;

    // HOW THE FUCK IS THIS THE CAUSE FOR NOT DELETING https://stackoverflow.com/a/67423651/4807235
    @OrderBy("id ASC")
    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SavedCategoryEntity> categories;

    public static SavedGameEntity ofId(int id) {
        final var savedGame = new SavedGameEntity();

        savedGame.setId(id);

        return savedGame;
    }
}
