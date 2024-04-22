package app.oengus.adapter.jpa.entity;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "incentive")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Incentive {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "marathon_id")
    @JsonBackReference
    @JsonView(Views.Public.class)
    private MarathonEntity marathon;

    @ManyToOne
    @JoinColumn(name = "schedule_line_id")
    @JsonView(Views.Public.class)
    private ScheduleLine scheduleLine;

    @Column(name = "name")
    @JsonView(Views.Public.class)
    @NotBlank
    @Size(max = 50)
    private String name;

    @Column(name = "description")
    @JsonView(Views.Public.class)
    @NotBlank
    @Size(max = 300)
    private String description;

    @Column(name = "bid_war")
    @JsonView(Views.Public.class)
    private boolean bidWar;

    @Column(name = "locked")
    @JsonView(Views.Public.class)
    private boolean locked;

    @Column(name = "open_bid")
    @JsonView(Views.Public.class)
    private boolean openBid;

    @Column(name = "goal")
    @JsonView(Views.Public.class)
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal goal;

    @Column(name = "current_amount")
    @JsonView(Views.Public.class)
    @DecimalMin(value = "0.0")
    private BigDecimal currentAmount;

    @OneToMany(mappedBy = "incentive", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(value = "current_amount DESC")
    @JsonView(Views.Public.class)
    @JsonManagedReference("incentive")
    private List<Bid> bids;

    @JsonIgnore
    @OneToMany(mappedBy = "incentive", cascade = CascadeType.ALL)
    private List<DonationIncentiveLink> donationIncentiveLinks;

    @Transient
    private boolean toDelete = false;

    public int getId() {
        return this.id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public MarathonEntity getMarathon() {
        return this.marathon;
    }

    public void setMarathon(final MarathonEntity marathon) {
        this.marathon = marathon;
    }

    public List<Bid> getBids() {
        return this.bids;
    }

    public void setBids(final List<Bid> bids) {
        this.bids = bids;
    }

    public ScheduleLine getScheduleLine() {
        return this.scheduleLine;
    }

    public void setScheduleLine(final ScheduleLine scheduleLine) {
        this.scheduleLine = scheduleLine;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public boolean isBidWar() {
        return this.bidWar;
    }

    public void setBidWar(final boolean bidWar) {
        this.bidWar = bidWar;
    }

    public BigDecimal getGoal() {
        return this.goal;
    }

    public void setGoal(final BigDecimal goal) {
        this.goal = goal;
    }

    public BigDecimal getCurrentAmount() {
        return this.currentAmount;
    }

    public void setCurrentAmount(final BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void setLocked(final boolean locked) {
        this.locked = locked;
    }

    public boolean isToDelete() {
        return this.toDelete;
    }

    public void setToDelete(final boolean toDelete) {
        this.toDelete = toDelete;
    }

    public boolean isOpenBid() {
        return this.openBid;
    }

    public void setOpenBid(final boolean openBid) {
        this.openBid = openBid;
    }

    public List<DonationIncentiveLink> getDonationIncentiveLinks() {
        return this.donationIncentiveLinks;
    }

    public void setDonationIncentiveLinks(final List<DonationIncentiveLink> donationIncentiveLinks) {
        this.donationIncentiveLinks = donationIncentiveLinks;
    }
}
