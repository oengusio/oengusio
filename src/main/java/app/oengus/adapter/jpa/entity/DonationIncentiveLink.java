package app.oengus.adapter.jpa.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "donation_incentive_link")
public class DonationIncentiveLink {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "donation_incentive_link_id_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "incentive_id")
    private Incentive incentive;

    @ManyToOne
    @JoinColumn(name = "bid_id")
    private Bid bid;

    @Column(name = "amount")
    private BigDecimal amount;
}
