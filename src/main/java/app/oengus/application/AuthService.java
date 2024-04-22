package app.oengus.application;

import app.oengus.adapter.rest.dto.v2.auth.*;
import app.oengus.application.exception.InvalidMFACodeException;
import app.oengus.application.exception.InvalidPasswordException;
import app.oengus.application.exception.auth.UnknownServiceException;
import app.oengus.application.exception.auth.UserDisabledException;
import app.oengus.application.port.persistence.EmailVerificationPersistencePort;
import app.oengus.application.port.persistence.PasswordResetPersistencePort;
import app.oengus.application.port.security.JWTPort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.PendingEmailVerification;
import app.oengus.domain.PendingPasswordReset;
import app.oengus.spring.model.Role;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final TOTPService totpService;
    private final UserService userService;
    private final JWTPort jwtPort;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final DiscordService discordService;
    private final TwitchService twitchService;
    private final PasswordResetPersistencePort passwordResetPersistencePort;
    private final EmailVerificationPersistencePort emailVerificationPersistencePort;

    // TODO: make a custom model? According to clean architecture it is required.
    public LoginResponseDto login(LoginDto body) {
        final var optionalUser = this.userService.findByUsername(body.getUsername().toLowerCase(Locale.ROOT));

        if (optionalUser.isEmpty()) {
            throw new InvalidPasswordException();
        }

        final var user = optionalUser.get();

        // Fast return if the user does not have a password set
        if (StringUtils.isEmpty(user.getPassword())) {
            throw new InvalidPasswordException();
        }

        // Validate password before 2fa, that feels more secure
        // If the password fails, send them an error.
        if (!this.validatePassword(body.getPassword(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        if (!user.isEnabled() || user.getRoles().contains(Role.ROLE_BANNED)) {
            throw new UserDisabledException();
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
            .setToken(this.jwtPort.generateToken(user));
    }

    public LoginResponseDto loginWithService(final String service, final String code, final String baseUrl) {
        final OengusUser user = switch (service) {
            case "discord" -> this.discordService.login(code, baseUrl);
            case "twitch" -> this.twitchService.login(code, baseUrl);
            default -> throw new UnknownServiceException();
        };

        if (!user.isEnabled() || user.getRoles().contains(Role.ROLE_BANNED)) {
            throw new UserDisabledException();
        }

        // TODO: to MFA or to not MFA?

        return new LoginResponseDto()
            .setStatus(LoginResponseDto.Status.LOGIN_SUCCESS)
            .setToken(this.jwtPort.generateToken(user));
    }

    public SignupResponseDto.Status signUp(OengusUser newUser) {
        // Check if username is already taken
        if (this.userService.existsByUsername(newUser.getUsername())) {
            return SignupResponseDto.Status.USERNAME_TAKEN;
        }

        newUser.setUsername(
            newUser.getUsername().toLowerCase(Locale.ROOT)
        );

        final var updatedUser = this.userService.save(newUser);

        this.sendNewVerificationEmail(updatedUser);

        return SignupResponseDto.Status.SIGNUP_SUCCESS;
    }

    public void sendNewVerificationEmail(OengusUser user) {
        final var verificationHash = UUID.randomUUID().toString();
        final var emailVerification = new PendingEmailVerification(
            user, verificationHash, LocalDate.now()
        );

        this.emailVerificationPersistencePort.save(emailVerification);

        this.emailService.sendEmailVerification(
            user,
            verificationHash
        );
    }

    public PasswordResetResponseDto.Status initPasswordReset(PasswordResetRequestDto body) {
        final var searchUser = this.userService.findByEmail(body.getEmail());

        // Always tell the user that a password reset has been sent
        // I guess this prevents people from datamining emails.
        if (searchUser.isEmpty()) {
            return PasswordResetResponseDto.Status.PASSWORD_RESET_SENT;
        }

        final var user = searchUser.get();

        if (!user.isEmailVerified()) {
            return PasswordResetResponseDto.Status.EMAIL_VERIFICATION_REQUIRED;
        }

        final var passwordReset = new PendingPasswordReset(
            user,
            UUID.randomUUID().toString(),
            LocalDate.now()
        );

        this.passwordResetPersistencePort.save(passwordReset);

        this.emailService.sendPasswordReset(user, passwordReset.token());

        return PasswordResetResponseDto.Status.PASSWORD_RESET_SENT;
    }

    // TODO: make this a bit more secure
    //  In theory people can reset the password for random users. (not sure how guessable UUID 4 is)
    //  But since this is (probably) not a security issue because they don't know who's password they just reset.
    public PasswordResetResponseDto.Status resetUserPassword(PasswordResetDto reset) {
        final var byToken = this.passwordResetPersistencePort.findByToken(reset.getToken());

        if (byToken.isEmpty()) {
            return PasswordResetResponseDto.Status.PASSWORD_RESET_CODE_INVALID;
        }

        final var passwordReset = byToken.get();
        final var user = passwordReset.user();

        user.setPassword(
            this.encodePassword(reset.getPassword())
        );

        this.userService.save(user);
        this.passwordResetPersistencePort.delete(passwordReset);

        return PasswordResetResponseDto.Status.PASSWORD_RESET_SUCCESS;
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
