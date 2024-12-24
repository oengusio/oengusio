package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.jpa.entity.comparator.AnswerComparator;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "answer")
public class AnswerEntity implements Comparable<AnswerEntity> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "submission_id")
    private SubmissionEntity submission;

    @Column(name = "answer")
    @Size(max = 500)
    private String answer;

    @AssertTrue
    public boolean isAnswerRequired() {
        if (this.question == null) { // WHY IS QUESTION NULL
            return false;
        }

        if (!this.question.isRequired()) {
            return true;
        }

        return StringUtils.isNotEmpty(this.answer);
    }

    @Override
    public int compareTo(final AnswerEntity o) {
        return new AnswerComparator().compare(this, o);
    }
}
