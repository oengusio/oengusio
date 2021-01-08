package app.oengus.web;

import app.oengus.entity.dto.UserProfileDto;
import app.oengus.entity.model.Error;
import app.oengus.entity.model.Marathon;
import app.oengus.entity.model.User;
import app.oengus.service.UserService;
import app.oengus.spring.model.LoginRequest;
import app.oengus.spring.model.Role;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.security.auth.login.LoginException;
import javax.validation.Valid;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Api(value = "/user")
public class UserController {

	@Autowired
	private UserService userService;

	@PostMapping("/login")
	@PermitAll
	@ApiIgnore
	public ResponseEntity<?> login(@RequestBody final LoginRequest request) {
		try {
			return ResponseEntity.ok(
					this.userService.login(request.getService(), request.getCode(), request.getOauthToken(),
							request.getOauthVerifier()));
		} catch (final LoginException e) {
			return ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
	}

	@PostMapping("/sync")
	@RolesAllowed({"ROLE_USER"})
	@PreAuthorize("!isBanned()")
	@ApiIgnore
	public ResponseEntity<?> sync(@RequestBody final LoginRequest request) {
		try {
			return ResponseEntity.ok(
					this.userService.sync(request.getService(), request.getCode(), request.getOauthToken(),
							request.getOauthVerifier()));
		} catch (final LoginException e) {
			return ResponseEntity.badRequest().body(new Error(e.getMessage()));
		}
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
			response = Marathon.class)
	public ResponseEntity<List<User>> search(@PathVariable("name") final String name) {
		return ResponseEntity.ok(this.userService.findUsersWithUsername(name));
	}

	@GetMapping("/{name}")
	@JsonView(Views.Public.class)
	@PermitAll
	@ApiOperation(value = "Get a user profile",
			response = UserProfileDto.class)
	public ResponseEntity<UserProfileDto> getUserProfile(@PathVariable("name") final String name) {
        final UserProfileDto userProfile = this.userService.getUserProfile(name);

        if (userProfile == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(userProfile);
	}

	@PatchMapping("/{id}")
	@PreAuthorize("isSelf(#id) && !isBanned()")
	@ApiIgnore
	public ResponseEntity<?> updateUser(@PathVariable("id") final Integer id,
	                                 @RequestBody @Valid final User userPatch,
	                                 final BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
		}
		try {
			this.userService.update(id, userPatch);
			return ResponseEntity.noContent().build();
		} catch (final NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/me")
	@RolesAllowed({"ROLE_USER"})
	@JsonView(Views.Internal.class)
	@ApiIgnore
	public ResponseEntity<User> me(final Principal principal) {
		try {
		    final int id = ((User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal()).getId();

			return ResponseEntity.ok(this.userService.getUser(id));
		} catch (final NotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

    @PostMapping("/ban/{id}")
    @PreAuthorize("isAdmin()")
    @ApiIgnore
	public ResponseEntity<?> ban(@PathVariable Integer id) {
        try {
            this.userService.addRole(id, Role.ROLE_BANNED);

            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/ban/{id}")
    @PreAuthorize("isAdmin()")
    @ApiIgnore
	public ResponseEntity<?> unban(@PathVariable Integer id) {
        try {
            this.userService.removeRole(id, Role.ROLE_BANNED);

            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
