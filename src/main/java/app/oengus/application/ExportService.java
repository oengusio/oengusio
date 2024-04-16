package app.oengus.application;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Game;
import app.oengus.domain.submission.Selection;
import app.oengus.domain.submission.Submission;
import app.oengus.entity.model.FieldType;
import app.oengus.entity.model.Status;
import app.oengus.helper.TimeHelpers;
import app.oengus.service.export.DonationsCsvExporter;
import app.oengus.service.export.ScheduleCsvExporter;
import app.oengus.service.export.ScheduleIcalExporter;
import app.oengus.service.export.ScheduleJsonExporter;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static app.oengus.helper.StringHelper.getUserDisplay;

@Service
@RequiredArgsConstructor
public class ExportService {
    private static final List<String> DEFAULT_HEADERS =
        List.of("runner", "game_name", "game_description", "game_console", "game_ratio", "category_name",
            "category_description", "category_type", "category_estimate", "category_video", "status");

    private final SubmissionService submissionService;
    private final MarathonPersistencePort marathonPersistencePort;
    private final DonationsCsvExporter donationsCsvExporter;
    private final ScheduleCsvExporter scheduleCsvExporter;
    private final ScheduleJsonExporter scheduleJsonExporter;
    private final ScheduleIcalExporter scheduleIcalExporter;

    public Writer exportSubmissionsToCsv(final String marathonId, final String zoneId, final String language)
        throws IOException {
        final List<Submission> submissions = this.submissionService.findAllByMarathon(marathonId);

        final StringWriter out = new StringWriter();
        if (!submissions.isEmpty()) {
            try (final CSVPrinter printer = new CSVPrinter(out,
                CSVFormat.RFC4180.builder()
                    .setHeader(this.getCsvHeadersForMarathon(marathonId))
                    .setQuoteMode(QuoteMode.NON_NUMERIC)
                    .build()
            )) {
                final var locale = Locale.forLanguageTag(language);
                final var resourceBundle = ResourceBundle.getBundle("export.Exports", locale);

                for (final Submission submission : submissions) {
                    printer.printRecords(
                        this.generateCsvRecordsForSubmission(submission, resourceBundle, zoneId)
                    );
                }
            }
        }

        return out;
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

    private String[] getCsvHeadersForMarathon(final String marathonId) {
        final List<String> headers = new ArrayList<>(DEFAULT_HEADERS);

        this.marathonPersistencePort.findById(marathonId).ifPresent((marathon) -> {
            if (!marathon.getQuestions().isEmpty()) {
                marathon.getQuestions()
                    .stream()
                    .filter((q) -> q.getFieldType() != FieldType.FREETEXT)
                    .forEach(
                        (question) -> headers.add(question.getLabel())
                    );
            }
        });

        headers.add("availabilities");

        return headers.toArray(String[]::new);
    }

    // TODO: split this up in more functions.
    private List<List<String>> generateCsvRecordsForSubmission(
        final Submission submission, final ResourceBundle lang, final String zoneId
    ) {
        final List<List<String>> records = new ArrayList<>();

        for (final Game game : submission.getGames()) {
            for (final Category category : game.getCategories()) {
                final List<String> record = new ArrayList<>();
                final var opponents = category.getOpponents();
                final var opponentNames = opponents
                    .stream()
                    .map(
                        (opponent) -> getUserDisplay(opponent.getSubmission().getUser())
                    )
                    .collect(Collectors.joining(", "));

                final var submitterDisplay = getUserDisplay(submission.getUser());

                record.add(
                    submitterDisplay +
                        (StringUtils.isEmpty(opponentNames) ? StringUtils.EMPTY : ", " + opponentNames)
                );
                record.add(game.getName());
                record.add(StringUtils.normalizeSpace(game.getDescription()));
                record.add(game.getConsole() + (game.isEmulated() ? "*" : ""));
                record.add(game.getRatio());
                record.add(category.getName());
                record.add(StringUtils.normalizeSpace(category.getDescription()));
                record.add(lang.getString("run.type." + category.getType().name()));
                record.add(TimeHelpers.formatDuration(category.getEstimate()));

                if (opponents.isEmpty()) {
                    record.add(category.getVideo());
                } else {
                    final StringBuilder videos = new StringBuilder(
                        submitterDisplay + ": " + category.getVideo() + " - "
                    );
                    opponents.forEach(opponent -> {
                        videos.append(getUserDisplay(opponent.getSubmission().getUser()))
                            .append(":  ")
                            .append(opponent.getVideo());

                        if (opponents.indexOf(opponent) != opponents.size() - 1) {
                            videos.append(" - ");
                        }
                    });
                    record.add(videos.toString());
                }

                // Selection might be null, ensure that the status defaults to "TO-DO"
                final var selection = Optional.ofNullable(category.getSelection()).orElseGet(() -> {
                    // ids are not used so no need setting them.
                    final var newSel = new Selection(-1, null, -1);

                    newSel.setStatus(Status.TODO);

                    return newSel;
                });

                final String statusName = selection.getStatus().name();
                record.add(lang.getString("run.status." + statusName));

                if (!submission.getAnswers().isEmpty()) {
                    // TODO: remove free text questions.
                    /*this.getAnswers()
                        .stream()
                        .filter((a) -> a.getQuestion().getFieldType() != FieldType.FREETEXT)
                        .forEach(
                            (answer) -> record.add(answer.getAnswer())
                        );*/
                }

                final var formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
                final var zone = ZoneId.of(zoneId);

                submission.getAvailabilities().forEach(
                    (availability) ->  record.add(
                        "%s/%s".formatted(
                            availability.getFrom().withZoneSameInstant(zone),
                            availability.getTo().withZoneSameInstant(zone)
                        )
                    )
                );

                records.add(record);
            }
        }

        return records;
    }
}