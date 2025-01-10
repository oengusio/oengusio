package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table(name = "donation_extra_data")
public class DonationExtraData implements Comparable<DonationExtraData> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;

    @Column(name = "answer")
    @Size(max = 500)
    private String answer;

    @AssertTrue
    public boolean isAnswerRequired() {
        if (this.question == null) {
            return false;
        }
        if (!this.question.isRequired()) {
            return true;
        }
        return StringUtils.isNotEmpty(this.answer);
    }

    @Override
    public int compareTo(final DonationExtraData o) {
        return Integer.compare(this.getQuestion().getPosition(), o.getQuestion().getPosition());
    }
}
