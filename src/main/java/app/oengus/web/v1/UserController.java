package app.oengus.web.v1;

import app.oengus.entity.dto.ApplicationUserInformationDto;
import app.oengus.entity.dto.PatreonStatusDto;
import app.oengus.entity.dto.UserDto;
import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.model.ApplicationUserInformation;
import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.UserService;
import app.oengus.service.repository.PatreonStatusRepositoryService;
import app.oengus.spring.model.LoginRequest;
import app.oengus.spring.model.Role;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

@Tag(name = "users-v1")
@RestController
@CrossOrigin(maxAge = 3600)
@RequestMapping({"/v1/users", "/users"})
public class UserController {
    private final UserService userService;
    private final List<String> oauthOrigins;
    private final PatreonStatusRepositoryService patreonStatusRepositoryService;
    private final OkHttpClient client = new OkHttpClient();

    public UserController(
        final UserService userService,
        @Value("${oengus.oauthOrigins}") final List<String> oauthOrigins,
        final PatreonStatusRepositoryService patreonStatusRepositoryService
    ) {
        this.userService = userService;
        this.oauthOrigins = oauthOrigins;
        this.patreonStatusRepositoryService = patreonStatusRepositoryService;
    }

    @PermitAll
    @Operation(hidden = true)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody final LoginRequest request, @RequestHeader("Origin") final String host) throws LoginException {
        if (!this.oauthOrigins.contains(host)) {
            throw new OengusBusinessException("ORIGIN_DISALLOWED " + host);
        }

        return ResponseEntity.ok(this.userService.login(host, request));
    }

    @Operation(hidden = true)
    @PostMapping("/sync")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("!isBanned()")
    public ResponseEntity<?> sync(@RequestBody final LoginRequest request, @RequestHeader("Origin") final String host) throws LoginException {
        if (!this.oauthOrigins.contains(host)) {
            throw new OengusBusinessException("ORIGIN_DISALLOWED");
        }

        return ResponseEntity.ok(this.userService.sync(host, request));
    }

    @GetMapping("/{name}/exists")
    @PermitAll
    @Operation(summary = "Check if username exists")
    public ResponseEntity<Map<String, Boolean>> exists(@PathVariable("name") final String name) {
        final Map<String, Boolean> response = new HashMap<>();

        response.put("exists", this.userService.exists(name));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}/search")
    @JsonView(Views.Public.class)
    @PermitAll
    @Operation(summary = "Get a list of users that include searched string in their username"/*,
        response = User.class,
        responseContainer = "List"*/)
    public ResponseEntity<List<User>> search(@PathVariable("name") final String name) {
        return ResponseEntity.ok(this.userService.findUsersWithUsername(name));
    }

    @GetMapping("/{name}")
    @JsonView(Views.Public.class)
    @PermitAll
    @Operation(summary = "Get a user profile"/*,
        response = UserProfileDto.class*/)
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable("name") final String name) throws NotFoundException {
        final UserProfileDto userProfile = this.userService.getUserProfile(name);

        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{name}/avatar")
    @PermitAll
    @Operation(summary = "Get a user's avatar")
    public ResponseEntity<?> getUserAvatar(@PathVariable("name") final String name, final HttpServletRequest httpServletRequest) {
        String url = httpServletRequest.getRequestURL().toString();

        if (!url.contains("v1")) {
            url = url.replace("users", "v1/users");
        }

        // Redirect the user to the v2 endpoint, saves duplicated code
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", url.replace("v1", "v2"))
            .build();
    }

    @Operation(hidden = true)
    @PutMapping("/{id}/patreon-status")
    @PreAuthorize("isSelf(#id) && !isBanned()")
    public ResponseEntity<?> updateUserPatreonStatus(
        @PathVariable("id") final int id,
        @RequestBody @Valid final PatreonStatusDto patch,
        final BindingResult bindingResult,
        final Principal principal
    ) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // fetch a fresh user
        final User user = this.userService.getUser(getUserFromPrincipal(principal).getId());

        if (!patch.getPatreonId().equals(user.getPatreonId())) {
            throw new OengusBusinessException("ACCOUNT_NOT_OWNED_BY_USER");
        }

        this.patreonStatusRepositoryService.update(patch);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isSelf(#id) && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> updateUser(@PathVariable("id") final int id,
                                        @RequestBody @Valid final UserDto userPatch,
                                        final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        this.userService.updateRequest(id, userPatch);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(isSelf(#id) && !isBanned()) || isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> deleteUser(@PathVariable("id") final int id) throws NotFoundException {
        this.userService.markDeleted(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @RolesAllowed({"ROLE_USER"})
    @JsonView(Views.Internal.class)
    @Operation(hidden = true)
    public ResponseEntity<User> me(final Principal principal) throws NotFoundException {
        final int id = getUserFromPrincipal(principal).getId();

        return ResponseEntity.ok(this.userService.getUser(id));
    }

    @PostMapping("/{id}/ban")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> ban(@PathVariable int id) throws NotFoundException {
        this.userService.addRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/ban")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> unban(@PathVariable int id) throws NotFoundException {
        this.userService.removeRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enabled")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> setEnabled(@PathVariable int id, @RequestParam("status") final boolean status) throws NotFoundException {
        final User patch = this.userService.getUser(id);

        patch.setEnabled(status);

        this.userService.update(id, patch);

        return ResponseEntity.noContent().build();
    }

    @Operation(hidden = true)
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @GetMapping("/me/application-info")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> getApplicationInfo(final Principal principal) throws NotFoundException {
        final User user = getUserFromPrincipal(principal);
        final ApplicationUserInformation infoForUser = this.userService.getApplicationInfo(user);

        return ResponseEntity.ok(infoForUser);
    }

    @Operation(hidden = true)
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @PostMapping("/me/application-info")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> updateApplicationInfo(
        final Principal principal,
        @RequestBody @Valid final ApplicationUserInformationDto infoPatch,
        final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        this.userService.updateApplicationInfo(
            getUserFromPrincipal(principal),
            infoPatch
        );

        return ResponseEntity.accepted().build();
    }
}
