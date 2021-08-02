package app.oengus.entity.model;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Size;

@Entity
@Table(name = "donation_extra_data")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class DonationExtraData implements Comparable<DonationExtraData> {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id = -1;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonView(Views.Public.class)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    @JsonBackReference(value = "donationReference")
    @JsonView(Views.Public.class)
    @org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
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

    public Question getQuestion() {
        return this.question;
    }

    public void setQuestion(final Question question) {
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
