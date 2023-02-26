package app.oengus.entity.dto.v1.answers;

import app.oengus.entity.model.Answer;

public class AnswerDto {
    private int id;
    private int questionId;
    private int submissionId;
    private String answer;
    private String username;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public static AnswerDto fromAnswer(Answer answer) {
        final AnswerDto dto = new AnswerDto();

        dto.setId(answer.getId());
        dto.setQuestionId(answer.getQuestion().getId());
        dto.setSubmissionId(answer.getSubmission().getId());
        dto.setAnswer(answer.getAnswer());
        dto.setUsername(answer.getSubmission().getUser().getUsername());

        return dto;
    }
}
