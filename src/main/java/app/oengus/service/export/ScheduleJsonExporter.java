package app.oengus.service.export;

import app.oengus.entity.dto.V1ScheduleDto;
import app.oengus.entity.dto.ScheduleLineDto;
import app.oengus.entity.dto.horaro.Horaro;
import app.oengus.entity.dto.horaro.HoraroEvent;
import app.oengus.entity.dto.horaro.HoraroItem;
import app.oengus.entity.dto.horaro.HoraroSchedule;
import app.oengus.adapter.jpa.entity.MarathonEntity;
import app.oengus.exception.OengusBusinessException;
import app.oengus.helper.StringHelper;
import app.oengus.service.repository.MarathonRepositoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ScheduleJsonExporter implements Exporter {

    private static final List<String> COLUMNS =
        List.of("runners", "game", "category", "type", "console", "custom_data", "[[options]]");

    private final ScheduleHelper scheduleHelper;
    private final ObjectMapper objectMapper;
    private final MarathonRepositoryService marathonRepositoryService;

    @Autowired
    public ScheduleJsonExporter(ScheduleHelper scheduleHelper, ObjectMapper objectMapper, MarathonRepositoryService marathonRepositoryService) {
        this.scheduleHelper = scheduleHelper;
        this.objectMapper = objectMapper;
        this.marathonRepositoryService = marathonRepositoryService;
    }

    @Override
    public Writer export(final String marathonId, final String zoneId, final String language) throws IOException, NotFoundException {
        final V1ScheduleDto scheduleDto = this.scheduleHelper.getSchedule(marathonId, zoneId);

        if (scheduleDto == null) {
            throw new NotFoundException("Schedule not found");
        }

        final MarathonEntity marathon = this.marathonRepositoryService.findById(marathonId);
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
        horaroSchedule.setColumns(COLUMNS.stream()
            .map(column -> column.contains("[[") ? column : resourceBundle.getString(
                "schedule.export.json.column." + column))
            .collect(Collectors.toList()));
        horaroSchedule.setItems(
            scheduleDto.getLinesWithTime()
                .stream()
                .map(
                    (scheduleLineDto) -> this.mapLineToItem(scheduleLineDto, resourceBundle)
                )
                .collect(Collectors.toList())
        );
        horaro.setSchedule(horaroSchedule);

        final StringWriter out = new StringWriter();
        out.append(this.objectMapper.writeValueAsString(horaro));
        return out;
    }

    private HoraroItem mapLineToItem(final ScheduleLineDto scheduleLineDto, final ResourceBundle resourceBundle) {
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
                        .map(StringHelper::getUserDisplay)
                        .collect(Collectors.joining(", "))
                ),
                StringUtils.defaultString(gameName),
                StringUtils.defaultString(scheduleLineDto.getCategoryName()),
                resourceBundle.getString("run.type." + scheduleLineDto.getType().name()),
                StringUtils.defaultString(scheduleLineDto.getConsole()),
                StringUtils.defaultString(scheduleLineDto.getCustomDataDTO()),
                this.objectMapper.writeValueAsString(
                    Map.of("setup", this.formatCustomSetupTime(scheduleLineDto.getEffectiveSetupTime()))
                )
            ));
        } catch (final JsonProcessingException e) {
            Sentry.captureException(e);
            throw new OengusBusinessException("EXPORT_FAIL");
        }
        return horaroItem;
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
