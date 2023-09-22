package app.oengus.web.v2;

import app.oengus.entity.dto.BooleanStatusDto;
import app.oengus.entity.dto.v2.auth.InitMFADto;
import app.oengus.entity.dto.v2.auth.LoginDto;
import app.oengus.entity.dto.v2.auth.LoginResponseDto;
import app.oengus.entity.dto.v2.auth.SignUpDto;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;

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
    default ResponseEntity<?> signUp(@RequestBody @Valid SignUpDto body) {
        return null;
    }

    @PutMapping("/mfa/init")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Set up MFA/2fa for your account.",
        responses = {
            @ApiResponse(
                description = "Mfa setting was started, please enter the secret key in your app and verify the code.",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = InitMFADto.class)
                )
            ),
            @ApiResponse(
                description = "You are not logged in",
                responseCode = "401"
            )
        }
    )
    ResponseEntity<InitMFADto> initMFA(final Principal principal) throws NotFoundException, IOException, WriterException;

    @DeleteMapping("/mfa")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Verify and enable MFA/2fa for your account.",
        responses = {
            @ApiResponse(
                description = "Status: true, 2fa has been enabled for your account. Status: false, 2fa code is invalid.",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            ),
            @ApiResponse(
                description = "Status: false, 2fa has not been initialized for your account, or you already have it enabled.",
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            ),
            @ApiResponse(
                description = "You are not logged in",
                responseCode = "401"
            )
        }
    )
    ResponseEntity<BooleanStatusDto> verifyAndStoreMFA(final Principal principal, @RequestParam final String code) throws NotFoundException;

    @DeleteMapping("/mfa")
    @PreAuthorize("isAuthenticated()")
    default ResponseEntity<?> removeMFA(final Principal principal, @RequestParam final String code) {
        return null;
    }
}
