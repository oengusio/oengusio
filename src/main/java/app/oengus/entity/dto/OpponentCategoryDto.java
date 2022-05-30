package app.oengus.entity.dto;

import app.oengus.entity.dto.v1.submissions.SubmissionUserDto;
import app.oengus.entity.model.Availability;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public class OpponentCategoryDto {
	@JsonView(Views.Public.class)
	private int id;

	@JsonView(Views.Public.class)
	private SubmissionUserDto user;

	@JsonView(Views.Public.class)
	private String video;

	@JsonView(Views.Public.class)
	private List<Availability> availabilities;

	public int getId() {
		return this.id;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public String getVideo() {
		return this.video;
	}

	public void setVideo(final String video) {
		this.video = video;
	}

	public SubmissionUserDto getUser() {
		return this.user;
	}

	public void setUser(final SubmissionUserDto user) {
		this.user = user;
	}

	public List<Availability> getAvailabilities() {
		return this.availabilities;
	}

	public void setAvailabilities(final List<Availability> availabilities) {
		this.availabilities = availabilities;
	}
}
