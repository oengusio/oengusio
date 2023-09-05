package app.oengus.service.auth;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

// https://medium.com/@ihorsokolyk/two-factor-authentication-with-java-and-google-authenticator-9d7ea15ffee6
@Service
public class TOTPService {
    private static final String ISSUER = "OengusIO";

    public String generateSecretKey() {
        final SecureRandom random = new SecureRandom();
        final byte[] bytes = new byte[20];
        final Base32 base32 = new Base32();

        random.nextBytes(bytes);

        return base32.encodeToString(bytes);
    }

    public String getTOTPCode(String secretKey) {
        final Base32 base32 = new Base32();
        final byte[] bytes = base32.decode(secretKey);
        final String hexKey = Hex.encodeHexString(bytes);

        return TOTP.getOTP(hexKey);
    }

    public String getGoogleAuthenticatorQRCode(String secretKey, String account) {
        return "otpauth://totp/"
            + URLEncoder.encode(ISSUER + ":" + account, StandardCharsets.UTF_8).replace("+", "%20")
            + "?secret=" + URLEncoder.encode(secretKey, StandardCharsets.UTF_8).replace("+", "%20")
            + "&issuer=" + URLEncoder.encode(ISSUER, StandardCharsets.UTF_8).replace("+", "%20");
    }

    public void createQRCode(String barCodeData, String filePath, int height, int width)
        throws WriterException, IOException {
        final BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            MatrixToImageWriter.writeToStream(matrix, "png", out);
        }
    }
}
