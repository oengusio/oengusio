package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.domain.marathon.FieldType;
import app.oengus.domain.marathon.QuestionType;

import java.util.List;

public record QuestionDto(
    int id,
    String label,
    boolean required,
    List<String> options,
    FieldType fieldType,
    QuestionType type,
    String description,
    int position
) {
}
