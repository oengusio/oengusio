package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.v1.UserDto;
import app.oengus.adapter.rest.dto.v1.V1UserDto;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.UserService;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.entity.dto.ApplicationUserInformationDto;
import app.oengus.entity.dto.PatreonStatusDto;
import app.oengus.entity.dto.UserProfileDto;
import app.oengus.exception.OengusBusinessException;
import app.oengus.service.repository.PatreonStatusRepositoryService;
import app.oengus.spring.model.LoginRequest;
import app.oengus.spring.model.Role;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "users-v1")
@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v1/users")
public class UserController {
    private final UserSecurityPort securityPort;
    private final UserDtoMapper mapper;
    private final UserService userService;
    private final PatreonStatusRepositoryService patreonStatusRepositoryService;

    @Value("${oengus.oauthOrigins}")
    private List<String> oauthOrigins;

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
        final var user = this.securityPort.getAuthenticatedUser();
        final Map<String, Boolean> response = new HashMap<>();

        if (user == null) {
            response.put("exists", this.userService.existsByUsername(name));
        } else {
            final String currentUsername = user.getUsername();

            // Bugfix, can't change capitalisation on own username
            // if the new username does not equal the old username, but it does equal it regarding of case.
            // Then the user changed the capitalisation of their username
            if (!name.equals(currentUsername) && name.equalsIgnoreCase(currentUsername)) {
                response.put("exists", false);
                response.put("super-hacky-code", true);
            } else {
                response.put("exists", this.userService.existsByUsername(name));
            }
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}/search")
    @JsonView(Views.Public.class)
    @PermitAll
    @Operation(summary = "Get a list of users that include searched string in their username"/*,
        response = User.class,
        responseContainer = "List"*/)
    public ResponseEntity<List<V1UserDto>> search(@PathVariable("name") final String name) {
        return ResponseEntity.ok(
            this.userService.searchByUsername(name)
                .stream()
                .map(this.mapper::fromDomain)
                .toList()
        );
    }

    @GetMapping("/{name}")
    @JsonView(Views.Public.class)
    @PermitAll
    @Operation(summary = "Get a user profile"/*,
        response = UserProfileDto.class*/)
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable("name") final String name) throws NotFoundException {
        final var user = this.userService.findByUsername(name).orElseThrow(
            () -> new NotFoundException("User not found")
        );
        final var userProfile = this.mapper.profileFromDomain(user);

        // TODO: apply moderated marathons and submissions

        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{name}/avatar")
    @PermitAll
    @Operation(summary = "Get a user's avatar")
    public ResponseEntity<?> getUserAvatar(@PathVariable("name") final String name, final HttpServletRequest httpServletRequest) {
        final String url = httpServletRequest.getRequestURL().toString();

        // Redirect the user to the v2 endpoint, saves duplicated code
        return ResponseEntity.status(HttpStatus.PERMANENT_REDIRECT)
            .header("Location", url.replace("v1", "v2"))
            .build();
    }

    @Operation(hidden = true)
    @PutMapping("/{id}/patreon-status")
    @PreAuthorize("isSelf(#id) && !isBanned()")
    public ResponseEntity<?> updateUserPatreonStatus(
        @PathVariable("id") final int id,
        @RequestBody @Valid final PatreonStatusDto patch,
        final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var user = this.securityPort.getAuthenticatedUser();

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
                                        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var currentUser = this.securityPort.getAuthenticatedUser();

        // TODO: properly compare functionality with old service
        this.mapper.applyV1Patch(currentUser, userPatch);

        this.userService.save(currentUser);

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
    public ResponseEntity<V1UserDto> me() {
        final var user = this.securityPort.getAuthenticatedUser();

        return ResponseEntity.ok(
            this.mapper.fromDomain(user)
        );
    }

    @PostMapping("/{id}/ban")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> ban(@PathVariable int id) {
        this.userService.addRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/ban")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> unban(@PathVariable int id) {
        this.userService.removeRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enabled")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> setEnabled(@PathVariable int id, @RequestParam("status") final boolean status) throws NotFoundException {
        final var user = this.userService.getById(id);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        user.setEnabled(status);

        this.userService.save(user);

        return ResponseEntity.noContent().build();
    }

    @Operation(hidden = true)
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @GetMapping("/me/application-info")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> getApplicationInfo() {
        // TODO: re-implement when we are actually doing applications
        return ResponseEntity.notFound().build();
    }

    @Operation(hidden = true)
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @PostMapping("/me/application-info")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> updateApplicationInfo(
        @RequestBody @Valid final ApplicationUserInformationDto infoPatch,
        final BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        // TODO: re-implement when we are actually doing applications

        return ResponseEntity.notFound().build();
    }
}
