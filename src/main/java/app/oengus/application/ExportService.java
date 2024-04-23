package app.oengus.application;

import app.oengus.application.export.*;
import javassist.NotFoundException;
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

    public Writer exportSubmissionsToCsv(final String marathonId, final String zoneId, final String language)
        throws IOException, NotFoundException {
        return this.submissionCsvExporter.export(marathonId, zoneId, language);
    }

    public Writer exportScheduleToCsv(final String marathonId, final String zoneId, final String locale)
        throws IOException, NotFoundException {
        return this.scheduleCsvExporter.export(marathonId, zoneId, locale);
    }

    public Writer exportScheduleToJson(final String marathonId, final String zoneId, final String locale)
        throws IOException, NotFoundException {
        return this.scheduleJsonExporter.export(marathonId, zoneId, locale);
    }

    public Writer exportScheduleToIcal(final String marathonId, final String zoneId, final String locale)
        throws IOException, NotFoundException {
        return this.scheduleIcalExporter.export(marathonId, zoneId, locale);
    }

    public Writer exportDonationsToCsv(final String marathonId, final String zoneId, final String locale)
        throws IOException {
        return this.donationsCsvExporter.export(marathonId, zoneId, locale);
    }
}
