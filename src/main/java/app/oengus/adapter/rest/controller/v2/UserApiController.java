package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.v2.users.ModeratedHistoryDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileHistoryDto;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.UserService;
import app.oengus.adapter.rest.dto.DataListDto;
import lombok.RequiredArgsConstructor;
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

@RequiredArgsConstructor
@RestController("v2UserController")
public class UserApiController implements UserApi {
    private final UserService userService;
    private final UserDtoMapper mapper;
    // TODO: automatically inject this
    private final OkHttpClient client = new OkHttpClient();

    @Override
    public ResponseEntity<ProfileDto> profileByName(final String name) {
        final var user = this.userService.findByUsername(name).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
        final var profile = this.mapper.v2ProfileFromDomain(user);

        return ResponseEntity.ok(profile);
    }

    @Override
    public ResponseEntity<byte[]> getUserAvatar(final String name) throws NoSuchAlgorithmException, IOException {
        final var user = this.userService.findByUsername(name).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        );
        final String mail = user.getEmail();

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
        final var history = this.userService.getSubmissionHistory(id);
        final var dtos = history.stream().map(this.mapper::fromDomain).toList();

        return ResponseEntity.ok(
            new DataListDto<>(dtos)
        );
    }

    @Override
    public ResponseEntity<DataListDto<ModeratedHistoryDto>> userModerationHistory(final int id) {
        final var history = this.userService.getModeratedHistory(id);
        // Only show public marathons
        final var dtos = history.stream()
            .filter((marathon) -> !marathon.isPrivate())
            .map(this.mapper::fromDomainMarathon)
            .toList();

        return ResponseEntity.ok(
            new DataListDto<>(dtos)
        );
    }
}
