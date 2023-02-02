package app.oengus.web.v2;

import app.oengus.entity.dto.v2.users.ModeratedHistoryDto;
import app.oengus.entity.dto.v2.users.ProfileDto;
import app.oengus.entity.dto.v2.users.ProfileHistoryDto;
import app.oengus.entity.model.User;
import app.oengus.service.UserService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Tag(name = "users-v2")
@RestController("v2UserController")
@RequestMapping("/v2/users")
public class UserController {
    private final UserService userService;
    // TODO: automatically inject this
    private final OkHttpClient client = new OkHttpClient();

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PermitAll
    @GetMapping("/{name}")
    @JsonView(Views.Public.class)
    @Operation(
        summary = "Get a user's profile by their username",
        responses = {
            @ApiResponse(description = "User profile", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDto.class))),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    public ResponseEntity<ProfileDto> profileByName(@PathVariable("name") final String name) throws NotFoundException {
        final ProfileDto profile = this.userService.getUserProfileV2(name);

        return ResponseEntity.ok(profile);
    }

    @PermitAll
    @GetMapping("/{name}/avatar")
    @Operation(
        summary = "Request a user's avatar",
        responses = {
            @ApiResponse(description = "The profile image", responseCode = "200", content = @Content(mediaType = "image/*")),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable("name") final String name) throws NotFoundException, NoSuchAlgorithmException, IOException {
        final User user = this.userService.findByUsername(name);
        final String mail = user.getMail();

        if (!user.isEnabled() || mail == null) {
            throw new NotFoundException("This user does not exist");
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

    @PermitAll
    @GetMapping("/{id}/submission-history")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<ProfileHistoryDto>> userSubmissionHistory(@PathVariable("id") final int id) {
        final List<ProfileHistoryDto> history = this.userService.getUserProfileHistory(id);

        return ResponseEntity.ok(history);
    }

    @PermitAll
    @GetMapping("/{id}/moderation-history")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<ModeratedHistoryDto>> userModerationHistory(@PathVariable("id") final int id) {
        final List<ModeratedHistoryDto> history = this.userService.getUserModeratedHistory(id);

        return ResponseEntity.ok(history);
    }
}
