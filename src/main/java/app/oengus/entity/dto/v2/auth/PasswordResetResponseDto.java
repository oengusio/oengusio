package app.oengus.entity.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Responses for the password reset flow.")
public class PasswordResetResponseDto {

    @Schema(description = "The status of the request.")
    private Status status;

    public enum Status {
        EMAIL_VERIFICATION_REQUIRED,
        PASSWORD_RESET_SENT,
        PASSWORD_RESET_CODE_INVALID,
        PASSWORD_RESET_SUCCESS
    }
}
