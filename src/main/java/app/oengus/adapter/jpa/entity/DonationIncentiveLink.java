package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "donation_incentive_link")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DonationIncentiveLink {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "donation_incentive_link_id_seq")
    private int id = -1;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    @JsonBackReference("donation")
    @JsonView(Views.Public.class)
    private Donation donation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "incentive_id")
    @JsonView(Views.Public.class)
    private Incentive incentive;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    @JsonView(Views.Public.class)
    private Bid bid;

    @Column(name = "amount")
    @JsonView(Views.Public.class)
    private BigDecimal amount;

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public Donation getDonation() {
        return this.donation;
    }

    public void setDonation(final Donation donation) {
        this.donation = donation;
    }

    public Incentive getIncentive() {
        return this.incentive;
    }

    public void setIncentive(final Incentive incentive) {
        this.incentive = incentive;
    }

    public Bid getBid() {
        return this.bid;
    }

    public void setBid(final Bid bid) {
        this.bid = bid;
    }

    public BigDecimal getAmount() {
        return this.amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}
