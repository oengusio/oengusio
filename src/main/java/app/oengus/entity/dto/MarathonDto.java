package app.oengus.entity.dto;

import app.oengus.entity.model.Marathon;

import java.math.BigDecimal;

public class MarathonDto extends Marathon {

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
