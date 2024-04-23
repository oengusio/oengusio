package app.oengus.adapter.rest.dto.v1;

import app.oengus.domain.submission.Availability;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class SubmissionDto {
    private int id;
    private V1UserDto user;
    // TODO: should be an actual model instead of domain model.
    private List<Availability> availabilities;
    private Set<V1GameDto> games;
    private Set<V1AnswerDto> answers;
    private Set<V1OpponentDto> opponents;

    // This is only for backwards compatibility
    public Set<V1OpponentDto> getOpponentDtos() {
        return this.opponents;
    }
}
