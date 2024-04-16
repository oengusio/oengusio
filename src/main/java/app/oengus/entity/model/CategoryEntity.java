package app.oengus.entity.model;

import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.domain.submission.RunType;
import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
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
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "category")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Public.class)
	private int id;

	@ManyToOne
	@JoinColumn(name = "game_id")
	@JsonBackReference
	@JsonView(Views.Public.class)
	private GameEntity game;

	@Column(name = "name")
	@JsonView(Views.Public.class)
	@NotBlank
	@Size(max = 100)
	private String name;

	@Column(name = "estimate")
	@JsonView(Views.Public.class)
	@NotNull
	@DurationMin(seconds = 1)
	private Duration estimate;

	@Column(name = "description")
	@JsonView(Views.Public.class)
	@Size(max = 300)
	private String description;

	@Column(name = "video")
	@JsonView(Views.Public.class)
	@Size(max = 100)
	private String video;

	@Column(name = "run_type")
	@JsonView(Views.Public.class)
	private RunType type;

	@Column(name = "code")
	@JsonView(Views.Public.class)
	@Size(max = 6)
	private String code;

	@OneToOne(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonView(Views.Internal.class)
	private SelectionEntity selection;

	@OneToMany(mappedBy = "category", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
	@JsonManagedReference
	@JsonView(Views.Public.class)
	private List<OpponentEntity> opponents;

	@Transient
	@JsonView(Views.Public.class)
	private List<OpponentCategoryDto> opponentDtos;

	@Transient
	@JsonView(Views.Public.class)
	private Status status;

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

    public CategoryEntity fresh(GameEntity parent) {
        final CategoryEntity category = new CategoryEntity();

        Hibernate.initialize(this.getOpponents());
        BeanUtils.copyProperties(this, category, "game");

        category.setGame(parent);

        return category;
    }
}

