package app.oengus.entity.model;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "opponent")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Opponent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonView(Views.Public.class)
	private int id;

	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "id")
	@JsonBackReference
	private CategoryEntity category;

	@ManyToOne
	@JoinColumn(name = "opponent_submission_id")
	@JsonView(Views.Public.class)
	@JsonBackReference(value = "opponentReference")
	private Submission submission;

	@Column(name = "video")
	@JsonView(Views.Public.class)
	@Size(max = 100)
	private String video;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public CategoryEntity getCategory() {
		return this.category;
	}

	public void setCategory(final CategoryEntity category) {
		this.category = category;
	}

	public String getVideo() {
		return this.video;
	}

	public void setVideo(final String video) {
		this.video = video;
	}

	public Submission getSubmission() {
		return this.submission;
	}

	public void setSubmission(final Submission submission) {
		this.submission = submission;
	}
}
