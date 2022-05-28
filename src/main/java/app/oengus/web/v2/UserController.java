package app.oengus.web.v2;

import app.oengus.entity.dto.v2.users.ProfileDto;
import app.oengus.service.UserService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.PermitAll;

@Tag(name = "users-v2")
@RestController("v2UserController")
@RequestMapping("/v2/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // TODO: separate routes for connections, history, moderated marathons and volunteering
    @PermitAll
    @GetMapping("/{name}")
    @JsonView(Views.Public.class)
    @Operation(
        summary = "Get a user's profile by their username"
    )
    public ResponseEntity<?> profileByName(@PathVariable("name") final String name) throws NotFoundException {
        final ProfileDto profile = this.userService.getUserProfileV2(name);

        return ResponseEntity.ok(profile);
    }
}
