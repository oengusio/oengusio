package app.oengus.entity.dto;

import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.entity.model.Availability;
import app.oengus.entity.model.Opponent;
import app.oengus.entity.model.Submission;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.List;

public class OpponentCategoryDto {
	@JsonView(Views.Public.class)
	private int id;

	@JsonView(Views.Public.class)
	private ProfileDto user;

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

	public ProfileDto getUser() {
		return this.user;
	}

	public void setUser(final ProfileDto user) {
		this.user = user;
	}

	public List<Availability> getAvailabilities() {
		return this.availabilities;
	}

	public void setAvailabilities(final List<Availability> availabilities) {
		this.availabilities = availabilities;
	}

    @Deprecated
    public static OpponentCategoryDto fromOpponent(Opponent opponent) {
        final OpponentCategoryDto dto = new OpponentCategoryDto();
        final Submission submission = opponent.getSubmission();

        dto.setId(opponent.getId());
        dto.setVideo(opponent.getVideo());
        dto.setUser(ProfileDto.fromUser(submission.getUser()));
        dto.setAvailabilities(submission.getAvailabilities());

        return dto;
    }
}
