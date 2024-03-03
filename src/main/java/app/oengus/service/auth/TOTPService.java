package app.oengus.service.auth;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

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

    public byte[] createQRCode(String barCodeData, int height, int width)
        throws WriterException, IOException {
        final String format = "png";
        final BitMatrix matrix = new MultiFormatWriter().encode(barCodeData, BarcodeFormat.QR_CODE, width, height);

        try (final ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            final BufferedImage image = toBufferedImage(matrix);

            if (!ImageIO.write(image, format, out)) {
                throw new IOException("Could not write an image of format " + format);
            }

            return out.toByteArray();
        }
    }

    public String createQRCodeBase64(String barCodeData, int height, int width)
        throws WriterException, IOException {
        return Base64.getEncoder().encodeToString(createQRCode(barCodeData, height, width));
    }

    // Code below has been inspired from google's zxing javase code. See the following url for license information and source code:
    // https://github.com/zxing/zxing/blob/master/javase/src/main/java/com/google/zxing/client/j2se/MatrixToImageWriter.java#L60

    private BufferedImage toBufferedImage(BitMatrix matrix) {
        final int width = matrix.getWidth();
        final int height = matrix.getHeight();
        final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        final int[] rowPixels = new int[width];
        BitArray row = new BitArray(width);

        for (int y = 0; y < height; y++) {
            row = matrix.getRow(y, row);

            for (int x = 0; x < width; x++) {
                // HEX colour codes, first one is black, second one is white.
                // First two bytes are transparency
                rowPixels[x] = row.get(x) ? 0xFF000000 : 0xFFFFFFFF;
            }

            image.setRGB(0, y, width, 1, rowPixels, 0, width);
        }

        return image;
    }
}
