package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameCreateDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameUpdateDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

// TODO: documentation
@Tag(name = "user-saved-games")
@RequestMapping("/v2/users/@me/saved-games")
public interface UserSavedGamesApi {

    @GetMapping
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned() && isSupporter()")
    ResponseEntity<DataListDto<SavedGameDto>> getMySavedGames();

    @PostMapping
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned() && isSupporter()")
    ResponseEntity<SavedGameDto> create(@RequestBody @Valid SavedGameCreateDto body);

    @PatchMapping("/{gameId}")
    @PreAuthorize("hasVerifiedEmailAndIsNotBanned() && isSupporter()")
    ResponseEntity<SavedGameDto> update(@PathVariable int gameId, @RequestBody @Valid SavedGameUpdateDto body);

    // TODO: update category

    // TODO: delete game

    // TODO: delete category
}
