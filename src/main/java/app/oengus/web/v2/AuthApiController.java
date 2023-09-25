package app.oengus.web.v2;

import app.oengus.entity.dto.BooleanStatusDto;
import app.oengus.entity.dto.v2.auth.*;
import app.oengus.entity.dto.v2.users.ConnectionDto;
import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.UserService;
import app.oengus.service.auth.AuthService;
import app.oengus.service.auth.TOTPService;
import app.oengus.spring.JWTUtil;
import app.oengus.spring.model.Role;
import com.google.zxing.WriterException;
import javassist.NotFoundException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

@RestController
public class AuthApiController implements AuthApi {
    private final TOTPService totpService;
    private final AuthService authService;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public AuthApiController(
        TOTPService totpService, AuthService authService, UserService userService,
        final JWTUtil jwtUtil
    ) {
        this.totpService = totpService;
        this.authService = authService;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginDto body) {
        try {
            final String mfaCode = body.getTwoFactorCode();
            final User user = this.userService.findByUsername(body.getUsername());
            final String user2FaSecret = user.getMfaSecret();
            boolean userHas2fa = user.isMfaEnabled() && user2FaSecret != null;
            boolean mfaCodeCorrect = mfaCode == null;

            if (mfaCode == null && userHas2fa) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                        new LoginResponseDto()
                            .setStatus(LoginResponseDto.Status.MFA_REQUIRED)
                    );
            } else if (mfaCode != null && userHas2fa) {
                final String totpCode = this.totpService.getTOTPCode(user2FaSecret);

                if (!totpCode.equals(mfaCode)) {
                    return ResponseEntity
                        .status(HttpStatus.UNAUTHORIZED)
                        .body(
                            new LoginResponseDto()
                                .setStatus(LoginResponseDto.Status.MFA_INVALID)
                        );
                }
            } else {
                throw new OengusBusinessException("2FA_STATE_INVALID");
            }

            if (this.authService.validatePassword(body.getPassword(), user.getPassword())) {
                // login successful, return body
                return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                        new LoginResponseDto()
                            .setStatus(LoginResponseDto.Status.LOGIN_SUCCESS)
                            .setToken(this.jwtUtil.generateToken(user))
                    );
            }

            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    new LoginResponseDto()
                        .setStatus(LoginResponseDto.Status.USERNAME_PASSWORD_INCORRECT)
                );
        } catch (NotFoundException ignored) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(
                    new LoginResponseDto()
                        .setStatus(LoginResponseDto.Status.USERNAME_PASSWORD_INCORRECT)
                );
        }
    }

    @Override
    public ResponseEntity<SignupResponseDto> signUp(SignUpDto body) {
        // Check if username is already taken

        if (this.userService.exists(body.getUsername())) {
            return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(
                    new SignupResponseDto().setStatus(SignupResponseDto.Status.USERNAME_TAKEN)
                );
        }

        final User user = new User();

        user.setRoles(List.of(Role.ROLE_USER));
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setDisplayName(body.getDisplayName());
        user.setUsername(body.getUsername());
        user.setMail(body.getEmail());
        user.setHashedPassword(
            this.authService.encodePassword(body.getPassword())
        );
        user.setCountry(body.getCountry());
        user.setPronouns(String.join(",", body.getPronouns()));
        user.setLanguagesSpoken(body.getLanguagesSpoken());
        user.setConnections(
            body.getConnections()
                .stream()
                .map(ConnectionDto::toSocialAccount)
                .toList()
        );

        this.userService.update(user);

        // TODO: send verification email

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                new SignupResponseDto().setStatus(SignupResponseDto.Status.SIGNUP_SUCCESS)
            );
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
                        this.totpService.createQRCodeBase64(qrUrl, 500, 500)
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
    public ResponseEntity<?> removeMFA(Principal principal, String code) {
        return null;
    }
}
