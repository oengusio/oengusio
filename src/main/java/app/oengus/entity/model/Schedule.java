package app.oengus.entity.model;

import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "schedule")
public class Schedule {

	@Id
	@JsonView(Views.Public.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "marathon_id")
	@JsonBackReference
	@JsonView(Views.Public.class)
	private MarathonEntity marathon;

	@OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	@OrderBy("position ASC")
	@JsonView(Views.Public.class)
	private List<ScheduleLine> lines;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public MarathonEntity getMarathon() {
		return this.marathon;
	}

	public void setMarathon(final MarathonEntity marathon) {
		this.marathon = marathon;
	}

	public List<ScheduleLine> getLines() {
		return this.lines;
	}

	public void setLines(final List<ScheduleLine> lines) {
		this.lines = lines;
	}
}
