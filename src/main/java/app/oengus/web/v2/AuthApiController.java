package app.oengus.web.v2;

import app.oengus.entity.dto.BooleanStatusDto;
import app.oengus.entity.dto.v2.auth.*;
import app.oengus.entity.dto.v2.users.ConnectionDto;
import app.oengus.entity.model.User;
import app.oengus.helper.PrincipalHelper;
import app.oengus.service.UserService;
import app.oengus.service.auth.AuthService;
import app.oengus.service.auth.TOTPService;
import app.oengus.service.repository.UserRepositoryService;
import app.oengus.spring.JWTUtil;
import app.oengus.spring.model.Role;
import com.google.zxing.WriterException;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

@RestController
@RequiredArgsConstructor
public class AuthApiController implements AuthApi {
    private final TOTPService totpService;
    private final AuthService authService;
    private final UserService userService;
    private final UserRepositoryService userRepositoryService;
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
    public ResponseEntity<SignupResponseDto> signUp(@Valid SignUpDto body) {
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

        this.userRepositoryService.update(user);

        // TODO: send verification email

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(
                new SignupResponseDto().setStatus(SignupResponseDto.Status.SIGNUP_SUCCESS)
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
    public ResponseEntity<InitMFADto> initMFA(final Principal principal) throws NotFoundException, IOException, WriterException {
        final User principaluser = getUserFromPrincipal(principal);
        final User databaseUser = this.userRepositoryService.findById(principaluser.getId());

        final String newSecret = this.totpService.generateSecretKey();

        // Reset mfa for the user.
        databaseUser.setMfaEnabled(false);
        databaseUser.setMfaSecret(newSecret);

        this.userRepositoryService.update(databaseUser);

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
        final User databaseUser = this.userRepositoryService.findById(principaluser.getId());
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

        this.userRepositoryService.update(databaseUser);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<BooleanStatusDto> removeMFA(Principal principal, String code) throws NotFoundException {
        final User principaluser = getUserFromPrincipal(principal);
        final User databaseUser = this.userRepositoryService.findById(principaluser.getId());
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

        this.userRepositoryService.update(databaseUser);

        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new BooleanStatusDto(true));
    }
}
