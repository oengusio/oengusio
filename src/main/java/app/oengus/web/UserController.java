package app.oengus.web;

import app.oengus.entity.dto.ApplicationUserInformationDto;
import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.model.ApplicationUserInformation;
import app.oengus.entity.model.User;
import app.oengus.exception.OengusBusinessException;
import app.oengus.requests.user.UserUpdateRequest;
import app.oengus.service.UserService;
import app.oengus.spring.model.LoginRequest;
import app.oengus.spring.model.Role;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.security.auth.login.LoginException;
import javax.validation.Valid;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static app.oengus.helper.PrincipalHelper.getUserFromPrincipal;

@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/users")
@Api
public class UserController {
    private final UserService userService;
    private final List<String> oauthOrigins;
    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    public UserController(final UserService userService, @Value("${oengus.oauthOrigins}") final List<String> oauthOrigins) {
        this.userService = userService;
        this.oauthOrigins = oauthOrigins;
    }

    @PostMapping("/login")
    @PermitAll
    @ApiIgnore
    public ResponseEntity<?> login(@RequestBody final LoginRequest request, @RequestHeader("Origin") final String host) throws LoginException {
        if (!this.oauthOrigins.contains(host)) {
            throw new OengusBusinessException("ORIGIN_DISALLOWED");
        }

        return ResponseEntity.ok(this.userService.login(host, request));
    }

    @PostMapping("/sync")
    @RolesAllowed({"ROLE_USER"})
    @PreAuthorize("!isBanned()")
    @ApiIgnore
    public ResponseEntity<?> sync(@RequestBody final LoginRequest request, @RequestHeader("Origin") final String host) throws LoginException {
        if (!this.oauthOrigins.contains(host)) {
            throw new OengusBusinessException("ORIGIN_DISALLOWED");
        }

        return ResponseEntity.ok(this.userService.sync(host, request));
    }

    @GetMapping("/{name}/exists")
    @PermitAll
    @ApiOperation(value = "Check if username exists")
    public ResponseEntity<Map<String, Boolean>> exists(@PathVariable("name") final String name) {
        final Map<String, Boolean> validationErrors = new HashMap<>();
        if (this.userService.exists(name)) {
            validationErrors.put("exists", true);
        }
        return ResponseEntity.ok(validationErrors);
    }

    @GetMapping("/{name}/search")
    @JsonView(Views.Public.class)
    @PermitAll
    @ApiOperation(value = "Get a list of users that include searched string in their username",
        response = User.class,
        responseContainer = "List")
    public ResponseEntity<List<User>> search(@PathVariable("name") final String name) {
        return ResponseEntity.ok(this.userService.findUsersWithUsername(name));
    }

    @GetMapping("/{name}")
    @JsonView(Views.Public.class)
    @PermitAll
    @ApiOperation(value = "Get a user profile",
        response = UserProfileDto.class)
    public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable("name") final String name) throws NotFoundException {
        final UserProfileDto userProfile = this.userService.getUserProfile(name);

        return ResponseEntity.ok(userProfile);
    }

    @GetMapping("/{name}/avatar")
    @PermitAll
    @ApiOperation(value = "Get a user's avatar")
    public ResponseEntity<byte[]> getUserAvatar(@PathVariable("name") final String name) throws NotFoundException, NoSuchAlgorithmException, IOException {
        final User user = this.userService.findByUsername(name);

        // Strip off any "+blah" parts with the regex
        final String emailLower = user.getMail().toLowerCase().trim().replaceAll("\\+.*@", "@");
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

    @PatchMapping("/{id}")
    @PreAuthorize("isSelf(#id) && !isBanned()")
    @ApiIgnore
    public ResponseEntity<?> updateUser(@PathVariable("id") final int id,
                                        @RequestBody @Valid final UserUpdateRequest userPatch,
                                        final BindingResult bindingResult) throws NotFoundException {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        this.userService.updateRequest(id, userPatch);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(isSelf(#id) && !isBanned()) || isAdmin()")
    @ApiIgnore
    public ResponseEntity<?> deleteUser(@PathVariable("id") final int id) throws NotFoundException {
        this.userService.markDeleted(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    @RolesAllowed({"ROLE_USER"})
    @JsonView(Views.Internal.class)
    @ApiIgnore
    public ResponseEntity<User> me(final Principal principal) throws NotFoundException {
        final int id = getUserFromPrincipal(principal).getId();

        return ResponseEntity.ok(this.userService.getUser(id));
    }

    @PostMapping("/{id}/ban")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @ApiIgnore
    public ResponseEntity<?> ban(@PathVariable int id) throws NotFoundException {
        this.userService.addRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/ban")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @ApiIgnore
    public ResponseEntity<?> unban(@PathVariable int id) throws NotFoundException {
        this.userService.removeRole(id, Role.ROLE_BANNED);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/enabled")
    @PreAuthorize("isAuthenticated() && isAdmin()")
    @ApiIgnore
    public ResponseEntity<?> setEnabled(@PathVariable int id, @RequestParam("status") final boolean status) throws NotFoundException {
        final User patch = this.userService.getUser(id);

        patch.setEnabled(status);

        this.userService.update(id, patch);

        return ResponseEntity.noContent().build();
    }

    @ApiIgnore
    @PreAuthorize("isAuthenticated() && !isBanned()")
    @GetMapping("/me/application-info")
    @RolesAllowed({"ROLE_USER"})
    public ResponseEntity<?> getApplicationInfo(final Principal principal) throws NotFoundException {
        final User user = getUserFromPrincipal(principal);
        final ApplicationUserInformation infoForUser = this.userService.getApplicationInfo(user);

        return ResponseEntity.ok(infoForUser);
    }

    @ApiIgnore
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
