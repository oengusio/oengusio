package app.oengus.web.v2;

import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.dto.v2.users.ModeratedHistory;
import app.oengus.entity.dto.v2.users.ProfileDto;
import app.oengus.entity.dto.v2.users.ProfileHistory;
import app.oengus.service.UserService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;
import java.util.List;

@Tag(name = "users-v2")
@RestController("v2UserController")
@RequestMapping("/v2/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO: separate routes for connections?, history, moderated marathons and volunteering
    @PermitAll
    @GetMapping("/{name}")
    @JsonView(Views.Public.class)
    @Operation(
        summary = "Get a user's profile by their username",
        responses = {
            @ApiResponse(description = "User profile", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileDto.class))),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    public ResponseEntity<ProfileDto> profileByName(@PathVariable("name") final String name) throws NotFoundException {
        final ProfileDto profile = this.userService.getUserProfileV2(name);

        return ResponseEntity.ok(profile);
    }

    @PermitAll
    @GetMapping("/{id}/submission-history")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<ProfileHistory>> userSubmissionHistory(@PathVariable("id") final int id) {
        final List<ProfileHistory> history = this.userService.getUserProfileHistory(id);

        return ResponseEntity.ok(history);
    }

    @PermitAll
    @GetMapping("/{id}/moderated-history")
    @JsonView(Views.Public.class)
    public ResponseEntity<List<ModeratedHistory>> userModerationHistory(@PathVariable("id") final int id) {
        final List<ModeratedHistory> history = this.userService.getUserModeratedHistory(id);

        return ResponseEntity.ok(history);
    }
}
