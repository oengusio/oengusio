package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.BooleanStatusDto;
import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import app.oengus.adapter.rest.dto.v2.MarathonHomeDto;
import app.oengus.adapter.rest.dto.v2.marathon.MarathonSettingsDto;
import app.oengus.adapter.rest.dto.v2.marathon.QuestionDto;
import app.oengus.adapter.rest.dto.v2.marathon.request.ModeratorsUpdateRequest;
import app.oengus.adapter.rest.dto.v2.marathon.request.QuestionsUpdateRequest;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.rest.mapper.MarathonDtoMapper;
import app.oengus.adapter.rest.mapper.QuestionDtoMapper;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.MarathonService;
import app.oengus.domain.OengusUser;
import app.oengus.domain.exception.MarathonNotFoundException;
import app.oengus.domain.marathon.Marathon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@RestController("v2MarathonController")
@RequiredArgsConstructor
public class MarathonApiController implements MarathonApi {
    private final MarathonDtoMapper mapper;
    private final QuestionDtoMapper questionMapper;
    private final UserDtoMapper userDtoMapper;
    private final MarathonService marathonService;

    @Override
    public ResponseEntity<MarathonHomeDto> getMarathonsForHome() {
        final var next = this.marathonService.findNext();
        final var open = this.marathonService.findSubmitsOpen();
        final var live = this.marathonService.findLive();

        final Function<List<Marathon>, List<MarathonBasicInfoDto>> transform =
            (items) -> items.stream().map(this.mapper::toBasicInfo).toList();

        return ResponseEntity.ok()
            .headers(cachingHeaders(10, false))
            .body(
                new MarathonHomeDto(
                    transform.apply(live),
                    transform.apply(next),
                    transform.apply(open)
                )
            );
    }

    @Override
    public ResponseEntity<MarathonSettingsDto> getSettings(String marathonId) {
        final var marathon = this.marathonService.findById(marathonId)
            .orElseThrow(MarathonNotFoundException::new);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(
                this.mapper.toSettingsDto(marathon)
            );
    }

    @Override
    public ResponseEntity<MarathonSettingsDto> saveSettings(String marathonId, MarathonSettingsDto patch) {
        final String newMstdn = patch.getMastodon();

        // Prevent funny business with id
        patch.setId(marathonId);

        if (newMstdn != null && newMstdn.isBlank()) {
            patch.setMastodon(null);
        }

        final var marathon = this.marathonService.findById(marathonId)
            .orElseThrow(MarathonNotFoundException::new);

        this.mapper.applyUpdateRequest(marathon, patch);

        final var savedMarathon = this.marathonService.update(marathonId, marathon);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(
                this.mapper.toSettingsDto(savedMarathon)
            );
    }

    @Override
    public ResponseEntity<DataListDto<ProfileDto>> getModerators(String marathonId) {
        final var moderators = this.marathonService.findModerators(marathonId);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(
                new DataListDto<>(
                    moderators.stream()
                        .map(this.userDtoMapper::v2ProfileFromDomain)
                        .toList()
                )
            );
    }

    @Override
    public ResponseEntity<BooleanStatusDto> updateModerators(String marathonId, ModeratorsUpdateRequest body) {
        final var users = Arrays.stream(body.getUserIds()).mapToObj(OengusUser::new).toList();

        this.marathonService.setModerators(marathonId, users);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<BooleanStatusDto> removeModerator(String marathonId, int userId) {
        this.marathonService.removeModerator(marathonId, userId);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<DataListDto<QuestionDto>> getQuestions(String marathonId) {
        final var questions = this.marathonService.findQuestions(marathonId);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(
                new DataListDto<>(
                    questions.stream()
                        .map(this.questionMapper::toDto)
                        .toList()
                )
            );
    }

    @Override
    public ResponseEntity<BooleanStatusDto> updateQuestions(String marathonId, QuestionsUpdateRequest body) {
        this.marathonService.updateQuestions(
            marathonId,
            body.getQuestions()
                .stream()
                .map(this.questionMapper::fromDto)
                .peek((q) -> q.setMarathonId(marathonId))
                .toList()
        );

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<BooleanStatusDto> removeQuestion(String marathonId, int questionId) {
        this.marathonService.removeQuestion(marathonId, questionId);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new BooleanStatusDto(true));
    }
}
