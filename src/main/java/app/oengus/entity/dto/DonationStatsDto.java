package app.oengus.entity.dto;

import java.math.BigDecimal;

public class DonationStatsDto {

	private BigDecimal total;
	private BigDecimal average;
	private BigDecimal max;
	private int count;

	public BigDecimal getTotal() {
		return this.total;
	}

	public void setTotal(final BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getAverage() {
		return this.average;
	}

	public void setAverage(final BigDecimal average) {
		this.average = average;
	}

	public BigDecimal getMax() {
		return this.max;
	}

	public void setMax(final BigDecimal max) {
		this.max = max;
	}

	public int getCount() {
		return this.count;
	}

	public void setCount(final int count) {
		this.count = count;
	}
}
