package app.oengus.adapter.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "bid")
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_id_seq")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "incentive_id")
    private Incentive incentive;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Column(name = "current_amount")
    @DecimalMin(value = "0.0")
    private BigDecimal currentAmount;

    @Column(name = "approved")
    private boolean approved = false;

    @OneToMany(mappedBy = "bid")
    private List<DonationIncentiveLink> donationIncentiveLinks;

    @Transient
    private int incentiveId;

    @Transient
    private boolean toDelete;
}
