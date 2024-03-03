package app.oengus.entity.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema
public class SignupResponseDto {
    @Schema(description = "The result of this signup attempt")
    private Status status;

    public Status getStatus() {
        return status;
    }

    public SignupResponseDto setStatus(Status status) {
        this.status = status;
        return this;
    }

    public enum Status {
        SIGNUP_SUCCESS,
        USERNAME_TAKEN,
    }
}
