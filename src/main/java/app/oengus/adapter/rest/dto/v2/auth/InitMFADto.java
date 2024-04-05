package app.oengus.adapter.rest.dto.v2.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema
public class InitMFADto {
    @Schema(description = "The QR code encoded in Base64, data is for a PNG image.")
    private String qrCode;

    @Schema(description = "The secret key that can be manually entered in case the QR code is not working.")
    private String secretKey;

    public InitMFADto setQrCode(String qrCode) {
        this.qrCode = qrCode;
        return this;
    }

    public InitMFADto setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        return this;
    }
}
