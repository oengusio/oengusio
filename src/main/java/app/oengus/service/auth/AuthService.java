package app.oengus.service.auth;

import app.oengus.application.exception.InvalidMFACodeException;
import app.oengus.application.exception.InvalidPasswordException;
import app.oengus.entity.dto.v2.auth.LoginDto;
import app.oengus.entity.dto.v2.auth.LoginResponseDto;
import app.oengus.entity.model.User;
import app.oengus.service.UserService;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.JWTUtil;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TOTPService totpService;
    private final UserService userService;
    private final JWTUtil jwtUtil;
    private final UserRepositoryService userRepositoryService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginDto body) {
        try {
            final User user = this.userRepositoryService.findByUsername(body.getUsername());

            // Fast return if the user does not have a password set
            if (StringUtils.isEmpty(user.getPassword())) {
                throw new InvalidPasswordException();
            }

            // Validate password before 2fa, that feels more secure
            // If the password fails, send them an error.
            if (!this.validatePassword(body.getPassword(), user.getPassword())) {
                throw new InvalidPasswordException();
            }

            final String mfaCode = body.getTwoFactorCode();
            final String user2FaSecret = user.getMfaSecret();
            boolean userHas2fa = user.isMfaEnabled() && user2FaSecret != null;
            boolean mfaCodeCorrect = mfaCode == null;

            if (userHas2fa) {
                if (mfaCode == null) {
                    return new LoginResponseDto()
                        .setStatus(LoginResponseDto.Status.MFA_REQUIRED);
                } else {
                    final String totpCode = this.totpService.getTOTPCode(user2FaSecret);

                    if (!totpCode.equals(mfaCode)) {
                        throw new InvalidMFACodeException();
                    }
                }
            }

            // login successful, return body
            return new LoginResponseDto()
                .setStatus(LoginResponseDto.Status.LOGIN_SUCCESS)
                .setToken(this.jwtUtil.generateToken(user));
        } catch (NotFoundException ignored) {
            throw new InvalidPasswordException();
        }
    }

    public String encodePassword(String password) {
        return this.passwordEncoder.encode(password);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        return this.passwordEncoder.matches(password, encodedPassword);
    }
}
