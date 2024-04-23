package app.oengus.domain.marathon;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class Question {
    private final int id;
    private String marathonId;

    private String label;
    private boolean required;
    private List<String> options;
    private FieldType fieldType;
    private QuestionType type;
    private String description;
    private int position;
}
