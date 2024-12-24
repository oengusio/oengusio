package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "donation_extra_data")
public class DonationExtraData implements Comparable<DonationExtraData> {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id = -1;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonView(Views.Public.class)
    private QuestionEntity question;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    @JsonBackReference(value = "donationReference")
    @JsonView(Views.Public.class)
    private Donation donation;

    @Column(name = "answer")
    @JsonView(Views.Public.class)
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

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public QuestionEntity getQuestion() {
        return this.question;
    }

    public void setQuestion(final QuestionEntity question) {
        this.question = question;
    }

    public Donation getDonation() {
        return this.donation;
    }

    public void setDonation(final Donation donation) {
        this.donation = donation;
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(final String answer) {
        this.answer = answer;
    }

    @Override
    public int compareTo(final DonationExtraData o) {
        return Integer.compare(this.getQuestion().getPosition(), o.getQuestion().getPosition());
    }
}
