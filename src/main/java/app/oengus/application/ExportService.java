package app.oengus.application;

import app.oengus.application.export.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;

@Service
@RequiredArgsConstructor
public class ExportService {
    private final DonationsCsvExporter donationsCsvExporter;
    private final ScheduleCsvExporter scheduleCsvExporter;
    private final ScheduleJsonExporter scheduleJsonExporter;
    private final ScheduleIcalExporter scheduleIcalExporter;
    private final SubmissionCsvExporter submissionCsvExporter;

    public Writer exportSubmissionsToCsv(final String marathonId, final int itemId, final String zoneId, final String language)
        throws IOException {
        return this.submissionCsvExporter.export(marathonId, itemId, zoneId, language);
    }

    public Writer exportScheduleToCsv(final String marathonId, final int itemId, final String zoneId, final String locale)
        throws IOException {
        return this.scheduleCsvExporter.export(marathonId, itemId, zoneId, locale);
    }

    public Writer exportScheduleToJson(final String marathonId, final int itemId, final String zoneId, final String locale)
        throws IOException {
        return this.scheduleJsonExporter.export(marathonId, itemId, zoneId, locale);
    }

    public Writer exportScheduleToIcal(final String marathonId, final int itemId, final String zoneId, final String locale)
        throws IOException {
        return this.scheduleIcalExporter.export(marathonId, itemId, zoneId, locale);
    }

    public Writer exportDonationsToCsv(final String marathonId, final int itemId, final String zoneId, final String locale)
        throws IOException {
        return this.donationsCsvExporter.export(marathonId, itemId, zoneId, locale);
    }
}
