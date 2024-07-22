package app.oengus.adapter.rest.dto.v2.marathon.request;

import app.oengus.adapter.rest.dto.v2.marathon.QuestionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@Schema(description = "Update questions for a marathon")
public class QuestionsUpdateRequest {
    // TODO: limit the amount of questions that can be added
    @NotNull
    @Schema(description = "The questions to be updated, only questions of type SUBMISSION are supported", required = true)
    private List<QuestionDto> questions;
}
