package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameCreateDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameDto;
import app.oengus.adapter.rest.mapper.SavedGameDtoMapper;
import app.oengus.application.SavedGameService;
import app.oengus.application.port.security.UserSecurityPort;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserSavedGamesApiController implements UserSavedGamesApi {
    private final UserSecurityPort securityPort;
    private final SavedGameDtoMapper savedGameMapper;
    private final SavedGameService savedGameService;

    @Override
    public ResponseEntity<SavedGameDto> create(SavedGameCreateDto body) {
        // This will be -1 if we are not authenticated.
        // Unless spring is properly bugged that should never happen
        final var userId = this.securityPort.getAuthenticatedUserId();

        // (yeah I think I'm funny)
        final var unsavedSavedGame = this.savedGameMapper.createToDomain(body, userId);
        final var savedSavedGame = this.savedGameService.save(unsavedSavedGame);
        final var savedGameDto = this.savedGameMapper.fromDomain(savedSavedGame);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(savedGameDto);
    }
}
