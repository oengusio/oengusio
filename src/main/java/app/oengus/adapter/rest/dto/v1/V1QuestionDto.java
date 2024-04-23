package app.oengus.adapter.rest.dto.v1;

import app.oengus.domain.marathon.FieldType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
public class V1QuestionDto {
    private int id;

    @Size(max = 50)
    private String label;

    private FieldType fieldType;

    private boolean required;

    private List<@Size(max = 50) String> options;

    @NotBlank
    @Size(max = 10)
    private String questionType;

    @Size(max = 1000)
    private String description;

    private int position;
}
