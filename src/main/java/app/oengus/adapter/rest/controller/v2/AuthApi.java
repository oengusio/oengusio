package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.v2.auth.*;
import app.oengus.entity.dto.BooleanStatusDto;
import app.oengus.spring.model.LoginRequest;
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
                responseCode = "422",
                content = @Content(
                    mediaType = "application/json"
                )
            )
        }
    )
    ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto body);

    @PostMapping("/login/service")
    @PreAuthorize("isAnonymous()")
    ResponseEntity<LoginResponseDto> loginWithProvider(@RequestBody @Valid LoginRequest body);

    @PostMapping("/signup")
    @PreAuthorize("isAnonymous()")
    @Operation(
        summary = "Sign up for a new Oengus account.",
        responses = {
            @ApiResponse(
                description = "Signup was successful, check you email address for a verification link.",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = SignupResponseDto.class)
                )
            ),
            @ApiResponse(
                description = "You sent invalid data. (duncan document error response)",
                responseCode = "422",
                content = @Content(
                    mediaType = "application/json"
                )
            )
        }
    )
    ResponseEntity<SignupResponseDto> signUp(@RequestBody @Valid SignUpDto body);

    @PostMapping("/refresh-token")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Refresh the JWT token",
        responses = {
            @ApiResponse(
                description = "Login was successful",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginResponseDto.class)
                )
            )
        }
    )
    ResponseEntity<LoginResponseDto> refreshUserToken();

    @GetMapping("/verify-email/{hash}")
    @PreAuthorize("isAnonymous()")
    ResponseEntity<?> verifyEmail(@PathVariable final String hash) throws NotFoundException;

    @PostMapping("/verify-email")
    @PreAuthorize("!isBanned()")
    ResponseEntity<BooleanStatusDto> requestNewEmailVerification();

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
    ResponseEntity<InitMFADto> initMFA() throws IOException, WriterException;

    @PostMapping("/mfa")
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
                responseCode = "401",
                content = @Content(
                    mediaType = "application/json"
                )
            )
        }
    )
    ResponseEntity<BooleanStatusDto> verifyAndStoreMFA(@RequestParam final String code);

    @DeleteMapping("/mfa")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Verify and remove MFA/2fa for your account.",
        responses = {
            @ApiResponse(
                description = "Status: true, 2fa has been removed for your account. Status: false, 2fa code is invalid.",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            ),
            @ApiResponse(
                description = "Status: false, 2fa has not been initialized for your account.",
                responseCode = "400",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = BooleanStatusDto.class)
                )
            ),
            @ApiResponse(
                description = "You are not logged in",
                responseCode = "401",
                content = @Content(
                    mediaType = "application/json"
                )
            )
        }
    )
    ResponseEntity<BooleanStatusDto> removeMFA(@RequestParam final String code);

    @PostMapping("/password-reset/request")
    ResponseEntity<PasswordResetResponseDto> requestPasswordReset(@RequestBody @Valid PasswordResetRequestDto body);

    @PostMapping("/password-reset")
    ResponseEntity<PasswordResetResponseDto> completePasswordReset(@RequestBody @Valid PasswordResetDto body);
}
