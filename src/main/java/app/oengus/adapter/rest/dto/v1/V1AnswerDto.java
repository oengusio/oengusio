package app.oengus.adapter.rest.dto.v1;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class V1AnswerDto {
    private int id;
    @JsonView(Views.Internal.class)
    private V1QuestionDto question;
    private int submissionId;
    private String answer;
    private String username;

    public int getQuestionId() {
        return this.question.getId();
    }
}
