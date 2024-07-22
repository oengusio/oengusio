package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.domain.marathon.FieldType;
import app.oengus.domain.marathon.QuestionType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Schema(description = "Question that ia asked to a user during the submission process")
public record QuestionDto(
    @Schema(description = "The unique identifier of the question", required = true)
    int id,

    @NotNull
    @Size(max = 50)
    @Schema(description = "Label of this question", required = true)
    String label,

    @Schema(description = "True if the user must fill in this question", required = true)
    boolean required,

    @NotNull
    @Size(max = 50)
    @Schema(description = "Options for this question in case of a SELECT field type")
    List<String> options,

    @NotNull
    @Schema(description = "The type of question", required = true)
    FieldType fieldType,

    @NotNull
    @Schema(description = "SUBMISSION or DONATION", required = true)
    QuestionType type,

    @Size(max = 1000)
    @Schema(description = "Description shown to the user if question is FREETEXT. Only required if fieldType is FREETEXT")
    String description,

    @Schema(description = "The position of this question", required = true)
    int position
) {

    @JsonIgnore
    @AssertTrue(message = "Description must be set if question type is FREETEXT")
    public boolean isDescriptionValid() {
        return this.fieldType == FieldType.FREETEXT && this.description != null &&!this.description.isBlank();
    }
}
