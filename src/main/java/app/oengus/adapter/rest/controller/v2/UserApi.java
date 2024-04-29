package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.users.ModeratedHistoryDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileDto;
import app.oengus.adapter.rest.dto.v2.users.ProfileHistoryDto;
import app.oengus.domain.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.security.PermitAll;
import javax.validation.Valid;
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
}
