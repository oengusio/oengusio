package app.oengus.entity.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@Schema
public class LoginDto {
    @NotNull
    @NotBlank
    @Schema(description = "Your username")
    private String username;

    @NotNull
    @NotBlank
    // not limiting causes a security problem by forcing too much stuff into bcrypt.
    @Size(min = 8, max = 100)
    @Schema(description = "Your password")
    private String password;

    @Nullable
    @Schema(description = "Current 2fa code from authy, google autenticator or similar app.")
    private String twoFactorCode;
}
