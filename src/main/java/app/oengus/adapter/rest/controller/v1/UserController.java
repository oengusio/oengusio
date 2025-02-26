package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.v1.UserDto;
import app.oengus.adapter.rest.dto.v1.V1UserDto;
import app.oengus.adapter.rest.mapper.PatreonStatusDtoMapper;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.UserLookupService;
import app.oengus.application.UserService;
import app.oengus.application.port.persistence.PatreonStatusPersistencePort;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.adapter.rest.dto.ApplicationUserInformationDto;
import app.oengus.adapter.rest.dto.PatreonStatusDto;
import app.oengus.adapter.rest.dto.UserProfileDto;
import app.oengus.domain.exception.OengusBusinessException;
import app.oengus.adapter.rest.dto.v1.request.LoginRequest;
import app.oengus.domain.Role;
import app.oengus.adapter.rest.Views;
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

import jakarta.annotation.security.PermitAll;
import javax.security.auth.login.LoginException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@Tag(name = "users-v1")
@RestController
@RequiredArgsConstructor
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v1/users")
public class UserController {
    private final UserSecurityPort securityPort;
    private final UserDtoMapper mapper;
    private final UserService userService;
    private final UserLookupService userLookupService;
    private final PatreonStatusPersistencePort patreonStatusPersistencePort;
    private final PatreonStatusDtoMapper patreonStatusDtoMapper;

    @Value("${oengus.oauthOrigins}")
    private List<String> oauthOrigins;

    @Operation(hidden = true)
    @PostMapping("/sync")
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

        return ResponseEntity.ok()
            .headers(cachingHeaders(10))
            .body(response);
    }

    @GetMapping("/{name}/search")
    @JsonView(Views.Public.class)
    @PermitAll
    @Operation(summary = "Get a list of users that include searched string in their username"/*,
        response = User.class,
        responseContainer = "List"*/)
    public ResponseEntity<List<V1UserDto>> search(@PathVariable("name") final String name) {
        return ResponseEntity.ok()
            .headers(cachingHeaders(10))
            .body(
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

        return ResponseEntity.ok()
            .headers(cachingHeaders(30))
            .body(userProfile);
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
    @PreAuthorize("isSelf(#id)")
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

        final var pledge = this.patreonStatusDtoMapper.toDomain(patch);

        this.patreonStatusPersistencePort.save(pledge);

        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned() && isSelf(#id)")
    @Operation(hidden = true)
    public ResponseEntity<?> updateUser(@PathVariable("id") final int id,
                                        @RequestBody @Valid final UserDto userPatch,
                                        final BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        final var currentUser = this.securityPort.getAuthenticatedUser();

        // Unverify the email if they change it!
        // TODO: move this logic to the service!
        if (!userPatch.getEmail().equalsIgnoreCase(currentUser.getEmail())) {
            currentUser.setEmailVerified(false);
        }

        userPatch.setEnabled(currentUser.isEnabled());

        // TODO: properly compare functionality with old service
        this.mapper.applyV1Patch(currentUser, userPatch);

        this.userService.save(currentUser);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isSelfOrAdmin(#id)")
    @Operation(hidden = true)
    public ResponseEntity<?> deleteUser(@PathVariable("id") final int id) throws NotFoundException {
        this.userService.markDeleted(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @JsonView(Views.Internal.class)
    @Operation(hidden = true)
    public ResponseEntity<V1UserDto> me() {
        final var user = this.securityPort.getAuthenticatedUser();

        return ResponseEntity.ok(
            this.mapper.fromDomain(user)
        );
    }

    @PostMapping("/{id}/ban")
    @PreAuthorize("isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> ban(@PathVariable int id) {
        this.userService.addRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/ban")
    @PreAuthorize("isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> unban(@PathVariable int id) {
        this.userService.removeRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enabled")
    @PreAuthorize("isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> setEnabled(@PathVariable int id, @RequestParam("status") final boolean status) throws NotFoundException {
        final var user = this.userLookupService.getById(id);

        if (user == null) {
            throw new NotFoundException("User not found");
        }

        user.setEnabled(status);

        this.userService.save(user);

        return ResponseEntity.noContent().build();
    }

    @Operation(hidden = true)
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned()")
    @GetMapping("/me/application-info")
    public ResponseEntity<?> getApplicationInfo() {
        // TODO: re-implement when we are actually doing applications
        return ResponseEntity.notFound().build();
    }

    @Operation(hidden = true)
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned()")
    @PostMapping("/me/application-info")
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
