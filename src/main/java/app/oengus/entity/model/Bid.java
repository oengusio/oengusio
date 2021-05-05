package app.oengus.entity.model;

import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "bid")
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Bid {

	@Id
	@JsonView(Views.Public.class)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	@JoinColumn(name = "incentive_id")
	@JsonView(Views.Public.class)
	@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
	@JsonBackReference("incentive")
	private Incentive incentive;

	@Column(name = "name")
	@JsonView(Views.Public.class)
	@NotBlank
	private String name;

	@Column(name = "current_amount")
	@JsonView(Views.Public.class)
	@DecimalMin(value = "0.0")
	private BigDecimal currentAmount;

	@Column(name = "approved")
	@JsonView(Views.Public.class)
	private boolean approved;

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

	public void setId(final int id) {
		this.id = id;
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

	public void setApproved(final boolean approved) {
		this.approved = approved;
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
