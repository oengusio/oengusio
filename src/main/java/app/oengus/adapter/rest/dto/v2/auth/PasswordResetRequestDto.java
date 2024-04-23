package app.oengus.adapter.rest.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
@Schema(description = "Required data that is needed when a user requests a password reset.")
public class PasswordResetRequestDto {
    @Email
    @Schema(description = "The email of the user that wants to request the password reset. Must be a verified email address.")
    private String email;
}
