package app.oengus.adapter.jpa.entity;

import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "bid")
public class Bid {

    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bid_id_seq")
    private int id;

    @ManyToOne
    @JoinColumn(name = "incentive_id")
    @JsonView(Views.Public.class)
    @JsonBackReference("incentive")
    private Incentive incentive;

    @NotBlank
    @Column(name = "name")
    @JsonView(Views.Public.class)
    private String name;

    @Column(name = "current_amount")
    @JsonView(Views.Public.class)
    @DecimalMin(value = "0.0")
    private BigDecimal currentAmount;

    @Column(name = "approved")
    @JsonView(Views.Public.class)
    private boolean approved = false;

    @OneToMany(mappedBy = "bid")
    @JsonIgnore
    private List<DonationIncentiveLink> donationIncentiveLinks;

    @Transient
    private int incentiveId;

    @Transient
    private boolean toDelete;

    public int getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = Objects.requireNonNullElse(id, -1);
    }

    public Incentive getIncentive() {
        return this.incentive;
    }

    public void setIncentive(final Incentive incentive) {
        this.incentive = incentive;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public BigDecimal getCurrentAmount() {
        return this.currentAmount;
    }

    public void setCurrentAmount(final BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public boolean getApproved() {
        return this.approved;
    }

    public void setApproved(final Boolean approved) {
        this.approved = Objects.requireNonNullElse(approved, false);
    }

    public int getIncentiveId() {
        return this.incentiveId;
    }

    public void setIncentiveId(final int incentiveId) {
        this.incentiveId = incentiveId;
    }

    public boolean isToDelete() {
        return this.toDelete;
    }

    public void setToDelete(final boolean toDelete) {
        this.toDelete = toDelete;
    }
}
