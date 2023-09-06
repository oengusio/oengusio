package app.oengus.entity.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Schema
public class LoginDto {
    @NotNull
    @NotBlank
    @Schema(description = "Your username")
    private String username;

    @NotNull
    @NotBlank
    @Schema(description = "Your password")
    private String password;

    @Nullable
    @Schema(description = "Current 2fa code from authy, google autenticator or similar app.")
    private String twoFactorCode;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Nullable
    public String getTwoFactorCode() {
        return twoFactorCode;
    }

    public void setTwoFactorCode(@Nullable String twoFactorCode) {
        this.twoFactorCode = twoFactorCode;
    }
}
