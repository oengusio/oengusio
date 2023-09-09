package app.oengus.web.v2;

import app.oengus.entity.dto.v2.auth.LoginDto;
import app.oengus.entity.dto.v2.auth.LoginResponseDto;
import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.UserService;
import app.oengus.service.auth.AuthService;
import app.oengus.service.auth.TOTPService;
import app.oengus.spring.JWTUtil;
import javassist.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {
    private final TOTPService totpService;
    private final AuthService authService;
    private final UserService userService;
    private final JWTUtil jwtUtil;

    public AuthController(
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
}
