package app.oengus.service.auth;

import app.oengus.application.exception.InvalidMFACodeException;
import app.oengus.application.exception.InvalidPasswordException;
import app.oengus.entity.dto.v2.auth.*;
import app.oengus.entity.model.EmailVerification;
import app.oengus.entity.model.PasswordReset;
import app.oengus.entity.model.User;
import app.oengus.service.EmailService;
import app.oengus.service.UserService;
import app.oengus.service.repository.EmailVerificationRepositoryService;
import app.oengus.service.repository.PasswordResetRepositoryService;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.JWTUtil;
import app.oengus.spring.model.Role;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TOTPService totpService;
    private final UserService userService;
    private final PasswordResetRepositoryService passwordResetRepositoryService;
    private final UserRepositoryService userRepositoryService;
    private final JWTUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final EmailVerificationRepositoryService emailVerificationRepositoryService;

    public LoginResponseDto login(LoginDto body) {
        try {
            final User user = this.userService.findByUsername(body.getUsername());

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

    public SignupResponseDto.Status signUp(SignUpDto body) {
        // Check if username is already taken
        if (this.userService.exists(body.getUsername())) {
            return SignupResponseDto.Status.USERNAME_TAKEN;
        }

        final User user = new User();

        user.setRoles(List.of(Role.ROLE_USER));
        user.setEnabled(true);
        user.setEmailVerified(false);
        user.setDisplayName(body.getDisplayName());
        user.setUsername(body.getUsername());
        user.setMail(body.getEmail());
        user.setHashedPassword(
            this.encodePassword(body.getPassword())
        );
        user.setCountry(body.getCountry());

        if (body.getPronouns().isEmpty()) {
            user.setPronouns(null);
        } else {
            user.setPronouns(String.join(",", body.getPronouns()));
        }

        user.setLanguagesSpoken(body.getLanguagesSpoken());
        user.setConnections(
            body.getConnections()
                .stream()
                .map((connection) -> {
                    final var conn = connection.toSocialAccount();

                    conn.setUser(user);

                    return conn;
                })
                .toList()
        );

        final var updatedUser = this.userService.update(user);

        this.sendNewVerificationEmail(user);

        return SignupResponseDto.Status.SIGNUP_SUCCESS;
    }

    public void sendNewVerificationEmail(User user) {
        final var verificationHash = UUID.randomUUID().toString();
        final var emailVerification = new EmailVerification();

        emailVerification.setUser(user);
        emailVerification.setVerificationHash(verificationHash);

        this.emailVerificationRepositoryService.save(emailVerification);

        this.emailService.sendEmailVerification(
            user,
            verificationHash
        );
    }

    public PasswordResetResponseDto.Status initPasswordReset(PasswordResetRequestDto body) {
        final var searchUser = this.userRepositoryService.findByEmail(body.getEmail());

        // Always tell the user that a password reset has been sent
        // I guess this prevents people from datamining emails.
        if (searchUser.isEmpty()) {
            return PasswordResetResponseDto.Status.PASSWORD_RESET_SENT;
        }

        final var user = searchUser.get();

        if (!user.isEmailVerified()) {
            return PasswordResetResponseDto.Status.EMAIL_VERIFICATION_REQUIRED;
        }

        final var passwordReset = new PasswordReset();

        passwordReset.setUser(user);
        passwordReset.setToken(UUID.randomUUID().toString());

        final var updatedReset = this.passwordResetRepositoryService.save(passwordReset);

        this.emailService.sendPasswordReset(user, updatedReset.getToken());

        return PasswordResetResponseDto.Status.PASSWORD_RESET_SENT;
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
