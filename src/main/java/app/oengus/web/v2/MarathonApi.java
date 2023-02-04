package app.oengus.web.v2;

import app.oengus.entity.dto.v2.MarathonHomeDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.PermitAll;

@Tag(name = "marathons-v2")
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v2/marathons")
public interface MarathonApi {

    @PermitAll
    @GetMapping("/for-home")
    @Operation(
        summary = "Get marathons as shown on the front page. Has a 5 minute cache",
        responses = {
            @ApiResponse(
                description = "Marathons as shown on the front page.",
                responseCode = "200",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = MarathonHomeDto.class))
            )
        }
    )
    ResponseEntity<MarathonHomeDto> getMarathonsForHome();
}
