package app.oengus.adapter.rest.controller.v2;

import app.oengus.entity.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.users.ModeratedHistoryDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileHistoryDto;
import app.oengus.adapter.jpa.entity.User;
import app.oengus.service.UserService;
import javassist.NotFoundException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController("v2UserController")
public class UserApiController implements UserApi {
    private final UserService userService;
    // TODO: automatically inject this
    private final OkHttpClient client = new OkHttpClient();

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ResponseEntity<ProfileDto> profileByName(final String name) {
        final ProfileDto profile = this.userService.getUserProfileV2(name);

        if (profile == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<byte[]> getUserAvatar(final String name) throws NoSuchAlgorithmException, IOException {
        final User user;
        try {
            user = this.userService.findByUsername(name);
        } catch (NotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        final String mail = user.getMail();

        if (!user.isEnabled() || mail == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        // Strip off any "+blah" parts with the regex
        final String emailLower = mail.toLowerCase().trim().replaceAll("\\+.*@", "@");
        final byte[] md5s = MessageDigest.getInstance("MD5").digest(emailLower.getBytes());
        final String hash = DatatypeConverter.printHexBinary(md5s).toLowerCase();

        final Request request = new Request.Builder()
            .url("https://www.gravatar.com/avatar/" + hash + "?s=80&d=retro&r=pg")
            .get()
            // Send over the browser's user-agent
            .header("User-Agent", "oengus.io-gravatar-proxy/1.0")
            .build();

        try (final Response res = this.client.newCall(request).execute()) {
            try (final okhttp3.ResponseBody body = res.body()) {
                return ResponseEntity.status(HttpStatus.OK)
                    // copy over the headers we need
                    .header("Content-Type", res.header("Content-Type"))
                    .header("Cache-Control", res.header("Cache-Control"))
                    .header("Expires", res.header("Expires"))
                    .header("Last-Modified", res.header("Last-Modified"))
                    .header("Content-Length", res.header("Content-Length"))
                    .body(body.bytes());
            }
        }
    }

    @Override
    public ResponseEntity<DataListDto<ProfileHistoryDto>> userSubmissionHistory(final int id) {
        final List<ProfileHistoryDto> history = this.userService.getUserProfileHistory(id);

        return ResponseEntity.ok(
            new DataListDto<>(history)
        );
    }

    @Override
    public ResponseEntity<DataListDto<ModeratedHistoryDto>> userModerationHistory(final int id) {
        final List<ModeratedHistoryDto> history = this.userService.getUserModeratedHistory(id);

        return ResponseEntity.ok(
            new DataListDto<>(history)
        );
    }
}
