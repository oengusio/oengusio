package app.oengus.application.export;

import app.oengus.application.port.persistence.MarathonPersistencePort;
import app.oengus.application.port.persistence.SchedulePersistencePort;
import app.oengus.domain.schedule.Line;
import app.oengus.domain.schedule.Runner;
import app.oengus.domain.schedule.Schedule;
import app.oengus.domain.horaro.Horaro;
import app.oengus.domain.horaro.HoraroEvent;
import app.oengus.domain.horaro.HoraroItem;
import app.oengus.domain.horaro.HoraroSchedule;
import app.oengus.exception.OengusBusinessException;
import app.oengus.helper.ScheduleHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ScheduleJsonExporter implements Exporter {

    private static final List<String> COLUMNS =
        List.of("runners", "game", "category", "type", "console", "custom_data", "[[options]]");

    private final ObjectMapper objectMapper;
    private final MarathonPersistencePort marathonPersistencePort;
    private final SchedulePersistencePort schedulePersistencePort;

    @Override
    public Writer export(final String marathonId, final String zoneId, final String language) throws IOException, NotFoundException {
        final Schedule schedule = this.schedulePersistencePort.findFirstForMarathon(marathonId).orElseThrow(
            () -> new NotFoundException("Schedule not found")
        );
        final var marathon = this.marathonPersistencePort.findById(marathonId).orElseThrow(
            () -> new NotFoundException("Marathon not found")
        );

        final Locale locale = Locale.forLanguageTag(language);
        final ResourceBundle resourceBundle = ResourceBundle.getBundle("export.Exports", locale);
        final Horaro horaro = new Horaro();
        final HoraroSchedule horaroSchedule = new HoraroSchedule();
        final HoraroEvent horaroEvent = new HoraroEvent();

        horaroEvent.setName(marathon.getName());
        horaroEvent.setSlug(marathon.getId());

        horaroSchedule.setEvent(horaroEvent);
        horaroSchedule.setName(marathon.getName());
        horaroSchedule.setSlug(marathon.getId());
        horaroSchedule.setTimezone(zoneId);
        horaroSchedule.setStart(
            marathon
                .getStartDate()
                .withFixedOffsetZone()
                .withSecond(0)
                .format(DateTimeFormatter.ISO_DATE_TIME)
        );
        horaroSchedule.setTwitch(marathon.getTwitch());
        horaroSchedule.setTwitter(marathon.getTwitter());
        horaroSchedule.setColumns(
            COLUMNS.stream()
                .map(
                    (column) -> column.contains("[[")
                        ? column
                        : resourceBundle.getString("schedule.export.json.column." + column)
                )
                .toList()
        );

        final var rawLines = schedule.getLines();
        final var zonedLines = ScheduleHelper.mapTimeToZone(rawLines, zoneId);

        horaroSchedule.setItems(
            zonedLines
                .stream()
                .map(
                    (scheduleLineDto) -> this.mapLineToItem(scheduleLineDto, resourceBundle)
                )
                .toList()
        );

        horaro.setSchedule(horaroSchedule);

        final StringWriter out = new StringWriter();
        out.append(this.objectMapper.writeValueAsString(horaro));
        return out;
    }

    private HoraroItem mapLineToItem(final Line scheduleLineDto, final ResourceBundle resourceBundle) {
        final HoraroItem horaroItem = new HoraroItem();
        final boolean setupBlock = scheduleLineDto.isSetupBlock();

        // take the setup length for setup blocks
        if (setupBlock) {
            horaroItem.setLength(scheduleLineDto.getSetupTime().toString());
        } else {
            horaroItem.setLength(scheduleLineDto.getEstimate().toString());
        }

        final String gameName = setupBlock ? scheduleLineDto.getSetupBlockText() : scheduleLineDto.getGameName();

        try {
            horaroItem.setData(List.of(
                StringUtils.defaultString(
                    scheduleLineDto.getRunners()
                        .stream()
                        .map(Runner::getEffectiveDisplay)
                        .collect(Collectors.joining(", "))
                ),
                StringUtils.defaultString(gameName),
                StringUtils.defaultString(scheduleLineDto.getCategoryName()),
                resourceBundle.getString("run.type." + scheduleLineDto.getType().name()),
                StringUtils.defaultString(scheduleLineDto.getConsole()),
                StringUtils.defaultString(scheduleLineDto.getCustomData()),
                this.objectMapper.writeValueAsString(
                    Map.of(
                        "setup", this.formatCustomSetupTime(
                            getEffectiveSetupTime(scheduleLineDto)
                        )
                    )
                )
            ));
        } catch (final JsonProcessingException e) {
            Sentry.captureException(e);
            throw new OengusBusinessException("EXPORT_FAIL");
        }
        return horaroItem;
    }

    private Duration getEffectiveSetupTime(Line line) {
        if (line.isSetupBlock()) {
            return Duration.ZERO;
        }

        return line.getSetupTime();
    }

    private String formatCustomSetupTime(final Duration setupTime) {
        final long seconds = setupTime.getSeconds();
        return String.format(
            "%dh%02dm%02ds",
            seconds / 3600,
            (seconds % 3600) / 60,
            seconds % 60);
    }
}
