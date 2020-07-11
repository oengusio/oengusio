package app.oengus.entity.dto;

import app.oengus.entity.model.Game;
import app.oengus.entity.model.User;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;

public class GameDto extends Game {

	@JsonView(Views.Public.class)
	private User user;

	@JsonView(Views.Public.class)
	private Integer submissionId;

	public User getUser() {
		return this.user;
	}

	public void setUser(final User user) {
		this.user = user;
	}

	public Integer getSubmissionId() {
		return this.submissionId;
	}

	public void setSubmissionId(final Integer submissionId) {
		this.submissionId = submissionId;
	}
}
