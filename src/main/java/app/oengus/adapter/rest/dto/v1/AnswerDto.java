package app.oengus.adapter.rest.dto.v1;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnswerDto {
    private int id;
    private int questionId;
    private int submissionId;
    private String answer;
    private String username;
}
