package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.v1.MarathonBasicInfoDto;
import app.oengus.adapter.rest.dto.v2.MarathonHomeDto;
import app.oengus.adapter.rest.mapper.MarathonDtoMapper;
import app.oengus.application.MarathonService;
import app.oengus.domain.marathon.Marathon;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Function;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

/**
 * TODO: api ideas
 *  GET /v2/marathons/{ID}/moderators
 *  PUT /v2/marathons/{ID}/moderators Body: {"user_ids": [int...]}
 *  DELETE /v2/marathons/{ID}/moderators/{userID}
 *
 *  Separate routes for questions as well
 */
@RestController("v2MarathonController")
@RequiredArgsConstructor
public class MarathonApiController implements MarathonApi {
    private final MarathonDtoMapper mapper;
    private final MarathonService marathonService;

    @Override
    public ResponseEntity<MarathonHomeDto> getMarathonsForHome() {
        final var next = this.marathonService.findNext();
        final var open = this.marathonService.findSubmitsOpen();
        final var live = this.marathonService.findLive();

        final Function<List<Marathon>, List<MarathonBasicInfoDto>> transform =
            (items) -> items.stream().map(this.mapper::toBasicInfo).toList();

        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                new MarathonHomeDto(
                    transform.apply(next),
                    transform.apply(open),
                    transform.apply(live)
                )
            );
    }
}
