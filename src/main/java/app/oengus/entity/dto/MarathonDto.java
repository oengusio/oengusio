package app.oengus.entity.dto;

import app.oengus.entity.model.Marathon;

import java.math.BigDecimal;

public class MarathonDto extends Marathon {

	private BigDecimal donationsTotal;

	private Boolean hasSubmitted;

	public BigDecimal getDonationsTotal() {
		return this.donationsTotal;
	}

	public void setDonationsTotal(final BigDecimal donationsTotal) {
		this.donationsTotal = donationsTotal;
	}

	public Boolean getHasSubmitted() {
		return this.hasSubmitted;
	}

	public void setHasSubmitted(final Boolean hasSubmitted) {
		this.hasSubmitted = hasSubmitted;
	}
}
