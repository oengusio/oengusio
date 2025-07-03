package app.oengus.adapter.jpa.entity;

import app.oengus.domain.submission.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.time.DurationMin;

import java.time.Duration;

@Getter
@Setter
@Entity
@EqualsAndHashCode(exclude = { "game" })
@Table(name = "saved_categories")
public class SavedCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private SavedGameEntity game;

    @Column(name = "name")
    @NotBlank
    @Size(max = Category.NAME_MAX_LENGTH)
    private String name;

    @Column(name = "estimate")
    @NotNull
    @DurationMin(seconds = 1)
    private Duration estimate;

    @Column(name = "description")
    @Size(max = Category.DESCRIPTION_MAX_LENGTH)
    private String description;

    @Column(name = "video")
    @Size(max = Category.VIDEO_MAX_LENGTH)
    private String video;

}
