package app.oengus.service.export;

import app.oengus.entity.model.Donation;
import app.oengus.service.DonationService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

@Component
public class DonationsCsvExporter implements Exporter {

	@Autowired
	private DonationService donationService;

	@Override
	public Writer export(final String marathonId, final String zoneId, final String language) throws IOException {
		final StringWriter out = new StringWriter();
		Page<Donation> donations;
		int i = 0;
		do {
			donations = this.donationService.findForMarathon(marathonId, i, 100);
			if (donations.hasContent()) {
				try (final CSVPrinter printer = new CSVPrinter(out,
						CSVFormat.RFC4180.withHeader(donations.getContent().get(0).getCsvHeaders()).withQuoteMode(
								QuoteMode.NON_NUMERIC))) {
					for (final Donation donation : donations) {
						printer.printRecords(donation.getCsvRecords(zoneId));
					}
				}
				i++;
			}
		} while (donations.hasNext());

		return out;
	}
}
