package app.oengus.application.export;

import app.oengus.application.SubmissionService;
import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.FieldType;
import app.oengus.domain.submission.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static app.oengus.application.helper.StringHelper.getUserDisplay;

@Component
@RequiredArgsConstructor
public class SubmissionCsvExporter implements Exporter {
    private static final List<String> DEFAULT_HEADERS =
        List.of("runner", "game_name", "game_description", "game_console", "game_ratio", "category_name",
            "category_description", "category_type", "category_estimate", "category_video", "category_submitted_on", "status");

    private final MarathonPersistencePort marathonPersistencePort;
    private final SubmissionService submissionService;
    private final SubmissionPersistencePort submissionPersistencePort;

    @Override
    public Writer export(String marathonId, int itemId, String zoneId, String language) throws IOException {
        final List<Submission> submissions = this.submissionService.findAllByMarathon(marathonId);

        final StringWriter out = new StringWriter();
        if (!submissions.isEmpty()) {
            try (final CSVPrinter printer = new CSVPrinter(out,
                CSVFormat.RFC4180.builder()
                    .setHeader(this.getCsvHeadersForMarathon(marathonId))
                    .setQuoteMode(QuoteMode.NON_NUMERIC)
                    .get() // why the fuck change .build on a builder to .get????
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

    private List<List<String>> generateCsvRecordsForSubmission(
        final Submission submission, final ResourceBundle lang, final String zoneId
    ) {
        final List<List<String>> records = new ArrayList<>();
        final Map<Integer, String> opponentNameCache = new HashMap<>();

        if (submission.getGames().isEmpty()) {
            this.addEmptyGameForAnswers(submission, lang, zoneId, records);
        }

        for (final Game game : submission.getGames()) {
            this.addGame(submission, lang, zoneId, game, opponentNameCache, records);
        }

        return records;
    }

    private void addGame(Submission submission, ResourceBundle lang, String zoneId, Game game, Map<Integer, String> opponentNameCache, List<List<String>> records) {
        for (final Category category : game.getCategories()) {
            this.addCategory(submission, lang, zoneId, game, opponentNameCache, records, category);
        }
    }

    private void addCategory(Submission submission, ResourceBundle lang, String zoneId, Game game, Map<Integer, String> opponentNameCache, List<List<String>> records, Category category) {
        final List<String> record = new ArrayList<>();
        final var opponents = category.getOpponents();

        // Caching is king!
        opponents.forEach((opponent) -> {
            if (!opponentNameCache.containsKey(opponent.getUserId())) {
                final var oppUser = this.opponentToUser(opponent);
                final var display = getUserDisplay(oppUser);

                opponentNameCache.put(opponent.getUserId(), display);
            }
        });

        final var opponentNames = opponents
            .stream()
            .map(
                (opponent) -> opponentNameCache.get(opponent.getUserId())
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
                videos.append(opponentNameCache.get(opponent.getUserId()))
                    .append(":  ")
                    .append(opponent.getVideo());

                if (opponents.indexOf(opponent) != opponents.size() - 1) {
                    videos.append(" - ");
                }
            });
            record.add(videos.toString());
        }

        final var formattedTime = Optional.ofNullable(category.getCreatedAt())
                .orElseGet(() -> ZonedDateTime.ofInstant(Instant.EPOCH, ZoneOffset.UTC))
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        record.add(formattedTime);

        // Selection might be null, ensure that the status defaults to "TO-DO"
        final var selection = Optional.ofNullable(category.getSelection()).orElseGet(() -> {
            // ids are not used so no need setting them.
            final var newSel = new Selection(-1, -1);

            newSel.setStatus(Status.TODO);

            return newSel;
        });

        final String statusName = selection.getStatus().name();
        record.add(lang.getString("run.status." + statusName));

        this.addAnswersAndAvailability(submission, zoneId, record);

        records.add(record);
    }

    private void addAnswersAndAvailability(Submission submission, String zoneId, List<String> record) {
        final var answers = submission.getAnswers();

        if (!answers.isEmpty()) {
            answers.stream()
                .filter((a) -> a.getQuestion().getFieldType() != FieldType.FREETEXT)
                .forEach(
                    (answer) -> record.add(answer.getAnswer())
                );
        }

        final var zone = ZoneId.of(zoneId);
        final var availabilities = submission.getAvailabilities()
                .stream()
                .map(
                    (availability) ->
                        "%s/%s".formatted(
                            availability.getFrom().withZoneSameInstant(zone),
                            availability.getTo().withZoneSameInstant(zone)
                        )
                )
            .collect(Collectors.joining(", "));

        record.add(availabilities);
    }

    private void addEmptyGameForAnswers(Submission submission, ResourceBundle lang, String zoneId, List<List<String>> records) {
        final List<String> record = new ArrayList<>();
        final var rowMsg = lang.getString("export.msg.empty");

        record.add(getUserDisplay(submission.getUser()));

        // Insert empty rows for CSV padding
        record.add(rowMsg);
        record.add(rowMsg);
        record.add(rowMsg);
        record.add(rowMsg);
        record.add(rowMsg);
        record.add(rowMsg);
        record.add(rowMsg);
        record.add(rowMsg);

        record.add(rowMsg);
        record.add(rowMsg);

        record.add(lang.getString("run.status.TODO"));

        this.addAnswersAndAvailability(submission, zoneId, record);

        records.add(record);
    }

    private OengusUser opponentToUser(Opponent opponent) {
        return this.submissionPersistencePort.findById(opponent.getSubmissionId())
            .get()
            .getUser();
    }
}
