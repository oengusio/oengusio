package app.oengus.entity.model;

import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.*;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.util.List;

@Entity
@Table(name = "category")
@JsonIgnoreProperties(ignoreUnknown = true)
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Public.class)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "game_id")
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonBackReference
	@JsonView(Views.Public.class)
	private Game game;

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

	@OneToOne(mappedBy = "category", cascade = {CascadeType.ALL}, orphanRemoval = true)
	@JsonView(Views.Internal.class)
	private Selection selection;

	@OneToMany(mappedBy = "category", cascade = {CascadeType.REMOVE}, orphanRemoval = true)
	@JsonManagedReference
	@JsonView(Views.Public.class)
	private List<Opponent> opponents;

	@Transient
	@JsonView(Views.Public.class)
	private List<OpponentCategoryDto> opponentDtos;

	@Transient
	@JsonView(Views.Public.class)
	private Status status;

	public Integer getId() {
		return this.id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public Game getGame() {
		return this.game;
	}

	public void setGame(final Game game) {
		this.game = game;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public Duration getEstimate() {
		return this.estimate;
	}

	public void setEstimate(final Duration estimate) {
		this.estimate = estimate;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public String getVideo() {
		return this.video;
	}

	public void setVideo(final String video) {
		this.video = video;
	}

	public Selection getSelection() {
		return this.selection;
	}

	public void setSelection(final Selection selection) {
		this.selection = selection;
	}

	public RunType getType() {
		return this.type;
	}

	public void setType(final RunType type) {
		this.type = type;
	}

	public String getCode() {
		return this.code;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public List<Opponent> getOpponents() {
		return this.opponents;
	}

	public void setOpponents(final List<Opponent> opponents) {
		this.opponents = opponents;
	}

	public List<OpponentCategoryDto> getOpponentDtos() {
		return this.opponentDtos;
	}

	public void setOpponentDtos(final List<OpponentCategoryDto> opponentDtos) {
		this.opponentDtos = opponentDtos;
	}

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(final Status status) {
		this.status = status;
	}
}

