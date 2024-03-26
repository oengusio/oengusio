package app.oengus.web.v2;

import app.oengus.entity.dto.BooleanStatusDto;
import app.oengus.entity.dto.v2.auth.*;
import app.oengus.entity.model.User;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.UserService;
import app.oengus.service.auth.AuthService;
import app.oengus.service.auth.TOTPService;
import app.oengus.service.repository.EmailVerificationRepositoryService;
import app.oengus.spring.JWTUtil;
import app.oengus.spring.model.LoginRequest;
import com.google.zxing.WriterException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

// TODO: cleanup
@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {
    private final TOTPService totpService;
    private final AuthService authService;
    private final UserService userService;
    // TODO: hexagonal architecture.
    private final EmailVerificationRepositoryService emailVerificationRepositoryService;
    private final JWTUtil jwtUtil;

    @Override
    public ResponseEntity<LoginResponseDto> login(@Valid LoginDto body) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                this.authService.login(body)
            );
    }

    @Override
    public ResponseEntity<LoginResponseDto> loginWithProvider(LoginRequest body, HttpServletRequest request) throws MalformedURLException {
        final var url = new URL(request.getRequestURL().toString());
        final var port = url.getPort() > 0 ? ":" + url.getPort() : "";
        final var baseUrl = "%s://%s%s".formatted(url.getProtocol(), url.getHost(), port);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                this.authService.loginWithService(body.getService(), body.getCode(), baseUrl)
            );
    }

    @Override
    public ResponseEntity<SignupResponseDto> signUp(@Valid SignUpDto body) {
        final var status = this.authService.signUp(body);

        if (status == SignupResponseDto.Status.SIGNUP_SUCCESS) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(
                    new SignupResponseDto().setStatus(SignupResponseDto.Status.SIGNUP_SUCCESS)
                );
        }

        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(
                new SignupResponseDto().setStatus(status)
            );
    }

    @Override
    public ResponseEntity<LoginResponseDto> refreshUserToken() {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                new LoginResponseDto()
                    .setStatus(LoginResponseDto.Status.LOGIN_SUCCESS)
                    .setToken(this.jwtUtil.generateToken(PrincipalHelper.getCurrentUser()))
            );
    }

    @Override
    public ResponseEntity<?> verifyEmail(final String hash) throws NotFoundException {
        final var verification = this.emailVerificationRepositoryService.findByHash(hash)
            .orElseThrow(() -> new NotFoundException("Email verification not found."));
       final User user = verification.getUser();

       user.setEmailVerified(true);

       this.userService.update(user);
       this.emailVerificationRepositoryService.delete(verification);

        return ResponseEntity.ok()
            .body("<h1>Thank you for verifying your email address.</h1><p>If you haven't already, you can <a href=\"https://oengus.io/\">log-in on Oengus</a></p>");
    }

    @Override
    public ResponseEntity<BooleanStatusDto> requestNewEmailVerification(Principal principal) throws NotFoundException {
        final User principaluser = getUserFromPrincipal(principal);
        final User user = this.userService.getUser(principaluser.getId());

        if (user.isEmailVerified()) {
            return ResponseEntity
              .status(HttpStatus.BAD_REQUEST)
              .body(new BooleanStatusDto(false));
        }

        this.authService.sendNewVerificationEmail(user);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<InitMFADto> initMFA(final Principal principal) throws NotFoundException, IOException, WriterException {
        final User principaluser = getUserFromPrincipal(principal);
        final User databaseUser = this.userService.getUser(principaluser.getId());

        final String newSecret = this.totpService.generateSecretKey();

        // Reset mfa for the user.
        databaseUser.setMfaEnabled(false);
        databaseUser.setMfaSecret(newSecret);

        this.userService.update(databaseUser);

        final String qrUrl = this.totpService.getGoogleAuthenticatorQRCode(newSecret, databaseUser.getUsername());

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                new InitMFADto()
                    .setSecretKey(newSecret)
                    .setQrCode(
                        "data:image/png;base64," + this.totpService.createQRCodeBase64(qrUrl, 500, 500)
                    )
            );
    }

    @Override
    public ResponseEntity<BooleanStatusDto> verifyAndStoreMFA(final Principal principal, final String code) throws NotFoundException {
        final User principaluser = getUserFromPrincipal(principal);
        final User databaseUser = this.userService.getUser(principaluser.getId());
        final String mfaSecret = databaseUser.getMfaSecret();

        if (databaseUser.isMfaEnabled() || StringUtils.isBlank(mfaSecret)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BooleanStatusDto(false));
        }

        final String serverCode = this.totpService.getTOTPCode(mfaSecret);

        // In case of an invalid code, abort.
        if (!serverCode.equals(code)) {
            return ResponseEntity
               .status(HttpStatus.OK)
               .body(new BooleanStatusDto(false));
        }

        databaseUser.setMfaEnabled(true);

        this.userService.update(databaseUser);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<BooleanStatusDto> removeMFA(Principal principal, String code) throws NotFoundException {
        final User principaluser = getUserFromPrincipal(principal);
        final User databaseUser = this.userService.getUser(principaluser.getId());
        final String mfaSecret = databaseUser.getMfaSecret();

        if (!databaseUser.isMfaEnabled() || StringUtils.isBlank(mfaSecret)) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new BooleanStatusDto(false));
        }
        final String serverCode = this.totpService.getTOTPCode(mfaSecret);

        // In case of an invalid code, abort.
        if (!serverCode.equals(code)) {
            return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BooleanStatusDto(false));
        }

        databaseUser.setMfaEnabled(false);
        databaseUser.setMfaSecret(null);

        this.userService.update(databaseUser);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<PasswordResetResponseDto> requestPasswordReset(PasswordResetRequestDto body) {
        final var resetResponse = this.authService.initPasswordReset(body);

        if (resetResponse == PasswordResetResponseDto.Status.PASSWORD_RESET_SENT) {
            return ResponseEntity.ok(new PasswordResetResponseDto(resetResponse));
        }

        return ResponseEntity.badRequest().body(new PasswordResetResponseDto(resetResponse));
    }

    @Override
    public ResponseEntity<PasswordResetResponseDto> completePasswordReset(PasswordResetDto body) {
        final var res = this.authService.resetUserPassword(body);

        if (res == PasswordResetResponseDto.Status.PASSWORD_RESET_SUCCESS) {
            return ResponseEntity.ok(new PasswordResetResponseDto(res));
        }

        return ResponseEntity.badRequest().body(new PasswordResetResponseDto(res));
    }
}
