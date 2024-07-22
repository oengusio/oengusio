package app.oengus.adapter.rest.dto.v2.marathon.request;

import app.oengus.adapter.rest.dto.v2.marathon.QuestionDto;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionsUpdateRequest {
    private List<QuestionDto> questions;
}
