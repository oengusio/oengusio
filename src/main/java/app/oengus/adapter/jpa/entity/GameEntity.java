package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.Hibernate;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "game")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameEntity {

	@Id
	@JsonView(Views.Public.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "submission_id")
	@JsonBackReference
	@JsonView(Views.Public.class)
	private SubmissionEntity submission;

	@Column(name = "name")
	@JsonView(Views.Public.class)
	@NotBlank
	@Size(max = 100)
	private String name;

	@Column(name = "description")
	@JsonView(Views.Public.class)
	@NotBlank
	@Size(max = 500)
	private String description;

	@Column(name = "console")
	@JsonView(Views.Public.class)
	@NotBlank
	@Size(max = 20) // can grow upto 100 due to db type being varchar(100)
	private String console;

	@Column(name = "ratio")
	@JsonView(Views.Public.class)
	@NotBlank
	@Size(max = 10)
	private String ratio;

	@Column(name = "emulated")
	@JsonView(Views.Public.class)
	private boolean emulated;

	@OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@OrderBy("id ASC")
	@JsonView(Views.Public.class)
	private List<CategoryEntity> categories;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public SubmissionEntity getSubmission() {
		return this.submission;
	}

	public void setSubmission(final SubmissionEntity submission) {
		this.submission = submission;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getConsole() {
		return this.console;
	}

	public void setConsole(final String console) {
		this.console = console;
	}

	public String getRatio() {
		return this.ratio;
	}

	public void setRatio(final String ratio) {
		this.ratio = ratio;
	}

	public List<CategoryEntity> getCategories() {
		return this.categories;
	}

	public void setCategories(final List<CategoryEntity> categories) {
		this.categories = categories;
	}

	public boolean isEmulated() {
		return this.emulated;
	}

	public void setEmulated(final boolean emulated) {
		this.emulated = emulated;
	}

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

    public GameEntity fresh(boolean withSubmission) {
        return this.fresh(withSubmission, true);
    }

    public GameEntity fresh(boolean withSubmission, boolean withCategories) {
        final GameEntity game = new GameEntity();

        // load all the items needed from the old game
        Hibernate.initialize(this.getCategories());

        BeanUtils.copyProperties(this, game, "categories");

        if (withCategories) {
            // De-reference :D
            game.setCategories(
                this.getCategories().stream().map((c) -> c.fresh(game)).toList()
            );
        }

        if (withSubmission) {
            game.setSubmission(this.getSubmission().fresh(false));
        }

        return game;
    }

    public static void initialize(GameEntity game) {
        // load all the items needed from the old game
        Hibernate.initialize(game.getCategories());
        game.getCategories().forEach((category) -> {
            Hibernate.initialize(category.getOpponents());
        });
    }

    public static GameEntity ofId(int id) {
        final var game = new GameEntity();

        game.setId(id);

        return game;
    }
}
