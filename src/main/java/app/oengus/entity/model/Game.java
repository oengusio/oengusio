package app.oengus.entity.model;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "game")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Game {

	@Id
	@JsonView(Views.Public.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "submission_id")
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonBackReference
	@JsonView(Views.Public.class)
	private Submission submission;

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
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	private List<Category> categories;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Submission getSubmission() {
		return this.submission;
	}

	public void setSubmission(final Submission submission) {
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

	public List<Category> getCategories() {
		return this.categories;
	}

	public void setCategories(final List<Category> categories) {
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
        Game game = (Game) o;
        return emulated == game.emulated && Objects.equals(id, game.id) && Objects.equals(name, game.name) && Objects.equals(description, game.description) && Objects.equals(console, game.console) && Objects.equals(ratio, game.ratio) && Objects.equals(categories, game.categories);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, console, ratio, emulated, categories);
    }
}
