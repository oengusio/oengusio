package app.oengus.adapter.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "incentive")
public class Incentive {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    private MarathonEntity marathon;

    @ManyToOne
    @JoinColumn(name = "schedule_line_id")
    private ScheduleLine scheduleLine;

    @NotBlank
    @Column(name = "name")
    @Size(max = 50)
    private String name;

    @NotBlank
    @Column(name = "description")
    @Size(max = 300)
    private String description;

    @Column(name = "bid_war")
    private boolean bidWar;

    @Column(name = "locked")
    private boolean locked;

    @Column(name = "open_bid")
    private boolean openBid;

    @Column(name = "goal")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal goal;

    @DecimalMin(value = "0.0")
    @Column(name = "current_amount")
    private BigDecimal currentAmount;

    @OrderBy(value = "current_amount DESC")
    @OneToMany(mappedBy = "incentive", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids;

    @OneToMany(mappedBy = "incentive", cascade = CascadeType.ALL)
    private List<DonationIncentiveLink> donationIncentiveLinks;

    @Transient
    private boolean toDelete = false;
}
