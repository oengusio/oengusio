package app.oengus.adapter.rest.dto.v1;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SubmissionDto {
    private int id;
    private V1UserDto user;
    private Set<V1GameDto> games;
    private Set<V1AnswerDto> answers;
    private Set<V1OpponentDto> opponents;
}
