package app.oengus.web.v2;

import app.oengus.entity.dto.v2.auth.LoginDto;
import app.oengus.entity.dto.v2.auth.LoginResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Tag(name = "auth")
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v2/auth")
public interface AuthApi {

    @PostMapping("/login")
    @PreAuthorize("isAnonymous()")
    @Operation(
        summary = "Login with your Oengus credentials",
        responses = {
            @ApiResponse(
                description = "Login was successful",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class)
                )
            ),
            @ApiResponse(
                description = "Login was not successful, see status field for more details",
                responseCode = "401",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class)
                )
            ),
            @ApiResponse(
                description = "You sent invalid data. (duncan document error response)",
                responseCode = "422"
            )
        }
    )
    ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto body);

    // TODO
    default ResponseEntity<?> signUp(@RequestBody @Valid Object body) {
        return null;
    }
}
