package app.oengus.entity.model;

import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.entity.comparator.AnswerComparator;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "answer")
public class AnswerEntity implements Comparable<AnswerEntity> {

	@Id
	@JsonView(Views.Public.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@ManyToOne
	@JoinColumn(name = "question_id")
	@JsonView(Views.Public.class)
	private Question question;

	@ManyToOne
	@JoinColumn(name = "submission_id")
	@JsonBackReference(value = "answersReference")
	@JsonView(Views.Public.class)
	private SubmissionEntity submission;

	@Column(name = "answer")
	@JsonView(Views.Public.class)
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