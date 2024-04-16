package app.oengus.entity.dto;

import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.jpa.entity.AvailabilityEntity;
import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.domain.submission.Availability;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OpponentCategoryDto {
	@JsonView(Views.Public.class)
	private int id;

	@JsonView(Views.Public.class)
	private ProfileDto user;

	@JsonView(Views.Public.class)
	private String video;

	@JsonView(Views.Public.class)
	private List<Availability> availabilities;

    @Deprecated(forRemoval = true)
    public static OpponentCategoryDto fromOpponent(OpponentEntity opponent) {
        final OpponentCategoryDto dto = new OpponentCategoryDto();
        final SubmissionEntity submission = opponent.getSubmission();

        dto.setId(opponent.getId());
        dto.setVideo(opponent.getVideo());
        dto.setUser(ProfileDto.fromUser(submission.getUser()));
        dto.setAvailabilities(submission.getAvailabilities());

        return dto;
    }
}
