package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.users.*;
import app.oengus.adapter.rest.dto.v2.users.request.UserUpdateRequest;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameDto;
import app.oengus.domain.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Tag(name = "users-v2")
@CrossOrigin(maxAge = 3600)
@RequestMapping("/v2/users")
public interface UserApi {

    @PermitAll
    @GetMapping("/{name}")
    @Operation(
        summary = "Get a user's profile by their username",
        responses = {
            @ApiResponse(description = "User profile", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProfileDto.class))),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    ResponseEntity<ProfileDto> profileByName(@PathVariable("name") final String name);

    @GetMapping("/@me")
    @PreAuthorize("isAuthenticated()")
    @Operation(
        summary = "Get your own user information",
        responses = {
            @ApiResponse(description = "Internal user information", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SelfUserDto.class))),
            @ApiResponse(description = "You are not logged in", responseCode = "401")
        }
    )
    ResponseEntity<SelfUserDto> getMe();

    @PatchMapping("/{id}")
    @PreAuthorize("isSelfOrAdmin(#id)")
    @Operation(
        summary = "Get your own user information",
        responses = {
            @ApiResponse(description = "Internal user information", responseCode = "200", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SelfUserDto.class))),
            @ApiResponse(description = "User not found", responseCode = "404"),
            @ApiResponse(description = "You are not allowed to update this user", responseCode = "401")
        }
    )
    ResponseEntity<SelfUserDto> updateUser(@PathVariable("id") final int id, @Valid @RequestBody final UserUpdateRequest patch);

    @PermitAll
    @GetMapping("/{name}/avatar")
    @Operation(
        summary = "Request a user's avatar",
        responses = {
            @ApiResponse(description = "The profile image", responseCode = "200", content = @Content(mediaType = "image/*")),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    ResponseEntity<byte[]> getUserAvatar(@PathVariable("name") final String name) throws NoSuchAlgorithmException, IOException;

    @PermitAll
    @GetMapping("/{id}/submission-history")
    @Operation(
        summary = "Get a user's submission history by their ID",
        responses = {
            @ApiResponse(
                description = "Submission History",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ProfileHistoryDto.class)
                )
            ),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    ResponseEntity<DataListDto<ProfileHistoryDto>> userSubmissionHistory(@PathVariable("id") final int id);

    @PermitAll
    @GetMapping("/{id}/moderation-history")
    @Operation(
        summary = "Get a user's moderation history by their ID",
        responses = {
            @ApiResponse(
                description = "Moderation History",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ModeratedHistoryDto.class)
                )
            ),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    ResponseEntity<DataListDto<ModeratedHistoryDto>> userModerationHistory(@PathVariable("id") final int id);

    @Operation(hidden = true)
    @GetMapping("/{id}/roles")
    @PreAuthorize("isAdmin()")
    ResponseEntity<DataListDto<Role>> getUserRoles(@PathVariable("id") final int id);

    @Operation(hidden = true)
    @PutMapping("/{id}/roles")
    @PreAuthorize("isAdmin()")
    ResponseEntity<DataListDto<Role>> updateUserRoles(
        @PathVariable("id") final int id,
        @RequestBody @Valid final DataListDto<Role> roles
    );

    @Operation(hidden = true)
    @GetMapping("/{id}/supporter-status")
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned()")
    ResponseEntity<SupporterStatusDto> getUserSupporterStatus(@PathVariable final int id);

    // TODO: Delete saved games on user deletion
    /// //////
    // TODO: how to display in UI? Current idea is: {game.name} - {category.name} - {game.console}
    // TODO: rest of adding/removing of games and categories should be its own controller
    @Operation(hidden = true)
    @GetMapping("/@me/saved-games")
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned() && isSupporter()")
    ResponseEntity<DataListDto<SavedGameDto>> getMySavedGames(); // TODO: Move this route to UserSavedGamesApi?

    @GetMapping("/{id}/saved-games")
    @Operation(
        summary = "Get the saved games for a user by their id, has a 24 hour cache",
        responses = {
            @ApiResponse(
                description = "Submission History",
                responseCode = "200",
                content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(
                        schema = @Schema(
                            implementation = SavedGameDto.class
                        )
                    )
                )
            ),
            @ApiResponse(description = "User not found", responseCode = "404")
        }
    )
    ResponseEntity<DataListDto<SavedGameDto>> getAllSavedGames(@PathVariable final int id);

}
