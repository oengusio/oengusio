package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.v2.MarathonHomeDto;
import app.oengus.service.MarathonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import static app.oengus.helper.HeaderHelpers.cachingHeaders;

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

    private final MarathonService marathonService;

    @Override
    public ResponseEntity<MarathonHomeDto> getMarathonsForHome() {
        return ResponseEntity.ok()
            .headers(cachingHeaders(5, false))
            .body(
                this.marathonService.findMarathonsForHome()
            );
    }
}
