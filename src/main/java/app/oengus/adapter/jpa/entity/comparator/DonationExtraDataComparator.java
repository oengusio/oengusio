package app.oengus.adapter.jpa.entity.comparator;

import app.oengus.adapter.jpa.entity.DonationExtraData;

import java.util.Comparator;

public class DonationExtraDataComparator implements Comparator<DonationExtraData> {

	@Override
	public int compare(final DonationExtraData o1, final DonationExtraData o2) {
		return Integer.compare(o1.getQuestion().getPosition(), o2.getQuestion().getPosition());
	}
}
