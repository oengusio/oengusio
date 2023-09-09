package app.oengus.entity.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;

@Schema
public class LoginResponseDto {
    @Schema(description = "The current status of the login attempt")
    private Status status;

    @Nullable
    @Schema(description = "The authentication token for the user, only returned if the status is LOGIN_SUCCESS")
    private String token = null;

    // TODO: include user model?

    public Status getStatus() {
        return status;
    }

    public LoginResponseDto setStatus(Status status) {
        this.status = status;
        return this;
    }

    @Nullable
    public String getToken() {
        return token;
    }

    public LoginResponseDto setToken(@Nullable String token) {
        this.token = token;
        return this;
    }

    @Schema
    public enum Status {
        @Schema(description = "Returned when the account exists, but the application expects a 2fa code")
        MFA_REQUIRED,
        @Schema(description = "Returned when the 2fa code is no longer valid.")
        MFA_INVALID,
        @Schema(description = "Returned when the login attempt was successful, response will also include the authentication token.")
        LOGIN_SUCCESS,
        @Schema(description = "Returned when either the username or the password are incorrect.")
        USERNAME_PASSWORD_INCORRECT,
    }
}
