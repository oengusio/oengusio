package app.oengus.entity.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class LoginResponseDto {

    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        ACCOUNT_UNKNOWN,
        MFA_REQUIRED,
        LOGIN_SUCCESS,
        USERNAME_PASSWORD_INCORRECT,
    }
}
