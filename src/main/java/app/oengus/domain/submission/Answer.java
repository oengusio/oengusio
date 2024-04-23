package app.oengus.domain.submission;

import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Question;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Answer {
    private final int id;
    private final int submissionId;
    private OengusUser user;
    private Question question;
    private String answer;
}
