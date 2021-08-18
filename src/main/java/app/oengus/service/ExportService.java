package app.oengus.service;

import app.oengus.entity.model.Submission;
import app.oengus.service.export.DonationsCsvExporter;
import app.oengus.service.export.ScheduleCsvExporter;
import app.oengus.service.export.ScheduleIcalExporter;
import app.oengus.service.export.ScheduleJsonExporter;
import javassist.NotFoundException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

@Service
public class ExportService {

    private final SubmissionService submissionService;
    private final DonationsCsvExporter donationsCsvExporter;
    private final ScheduleCsvExporter scheduleCsvExporter;
    private final ScheduleJsonExporter scheduleJsonExporter;
    private final ScheduleIcalExporter scheduleIcalExporter;

    public ExportService(
        SubmissionService submissionService, DonationsCsvExporter donationsCsvExporter,
        ScheduleCsvExporter scheduleCsvExporter, ScheduleJsonExporter scheduleJsonExporter,
        ScheduleIcalExporter scheduleIcalExporter
    ) {
        this.submissionService = submissionService;
        this.donationsCsvExporter = donationsCsvExporter;
        this.scheduleCsvExporter = scheduleCsvExporter;
        this.scheduleJsonExporter = scheduleJsonExporter;
        this.scheduleIcalExporter = scheduleIcalExporter;
    }

    public Writer exportSubmissionsToCsv(final String marathonId, final String zoneId, final String language) throws IOException {
        final List<Submission> submissions = this.submissionService.findByMarathon(marathonId);
        final StringWriter out = new StringWriter();

        if (!submissions.isEmpty()) {
            try (final CSVPrinter printer = new CSVPrinter(
                out,
                CSVFormat.RFC4180.withHeader(submissions.get(0).getCsvHeaders()).withQuoteMode(QuoteMode.NON_NUMERIC)
            )) {
                final Locale locale = Locale.forLanguageTag(language);

                for (final Submission submission : submissions) {
                    printer.printRecords(submission.getCsvRecords(locale, zoneId));
                }
            }
        }

        return out;
    }

    public Writer exportScheduleToCsv(final String marathonId, final String zoneId, final String locale)
        throws IOException {
        return this.scheduleCsvExporter.export(marathonId, zoneId, locale);
    }

    public Writer exportScheduleToJson(final String marathonId, final String zoneId, final String locale)
        throws IOException, NotFoundException {
        return this.scheduleJsonExporter.export(marathonId, zoneId, locale);
    }

    public Writer exportScheduleToIcal(final String marathonId, final String zoneId, final String locale)
        throws IOException {
        return this.scheduleIcalExporter.export(marathonId, zoneId, locale);
    }

    public Writer exportDonationsToCsv(final String marathonId, final String zoneId, final String locale)
        throws IOException {
        return this.donationsCsvExporter.export(marathonId, zoneId, locale);
    }
}
