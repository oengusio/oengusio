package app.oengus.entity.dto;

import app.oengus.adapter.jpa.entity.MarathonEntity;

import java.math.BigDecimal;

public class MarathonDto extends MarathonEntity {

	private BigDecimal donationsTotal;

	private boolean hasSubmitted;

	public BigDecimal getDonationsTotal() {
		return this.donationsTotal;
	}

	public void setDonationsTotal(final BigDecimal donationsTotal) {
		this.donationsTotal = donationsTotal;
	}

	public boolean getHasSubmitted() {
		return this.hasSubmitted;
	}

	public void setHasSubmitted(final boolean hasSubmitted) {
		this.hasSubmitted = hasSubmitted;
	}
}
