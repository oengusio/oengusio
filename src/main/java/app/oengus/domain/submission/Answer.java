package app.oengus.domain.submission;

import app.oengus.adapter.jpa.entity.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Answer {
    private final int id;
    private final int questionId;
    private final int submissionId;
    private Question question;
    private String answer;
}
