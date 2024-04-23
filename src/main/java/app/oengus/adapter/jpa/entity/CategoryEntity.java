package app.oengus.adapter.jpa.entity;

import app.oengus.domain.submission.RunType;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "category")
public class CategoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameEntity game;

    @Column(name = "name")
    @NotBlank
    @Size(max = 100)
    private String name;

    @Column(name = "estimate")
    @NotNull
    @DurationMin(seconds = 1)
    private Duration estimate;

    @Column(name = "description")
    @Size(max = 300)
    private String description;

    @Column(name = "video")
    @Size(max = 100)
    private String video;

    @Column(name = "run_type")
    private RunType type;

    @Column(name = "code")
    @Size(max = 6)
    private String code;

    @OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private SelectionEntity selection;

    @OneToMany(mappedBy = "category", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
    private List<OpponentEntity> opponents = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryEntity category = (CategoryEntity) o;
        return Objects.equals(id, category.id) && Objects.equals(name, category.name) &&
            Objects.equals(estimate, category.estimate) && Objects.equals(description, category.description) &&
            Objects.equals(video, category.video) && type == category.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, estimate, description, video, type);
    }

    @Deprecated(forRemoval = true)
    public CategoryEntity fresh(GameEntity parent) {
        final CategoryEntity category = new CategoryEntity();

        Hibernate.initialize(this.getOpponents());
        BeanUtils.copyProperties(this, category, "game");

        category.setGame(parent);

        return category;
    }

    public static CategoryEntity ofId(int id) {
        final var category = new CategoryEntity();

        category.setId(id);

        return category;
    }
}

