package app.oengus.adapter.rest.dto.v2.auth;

import app.oengus.adapter.rest.dto.validation.ValidPassword;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@Schema(description = "The data we need to reset a user's password.'")
public class PasswordResetDto {
    @NotBlank
    private String token;

    @ValidPassword
    private String password;
}
