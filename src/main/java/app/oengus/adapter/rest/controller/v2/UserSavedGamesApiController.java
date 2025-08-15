package app.oengus.adapter.rest.controller.v2;

import app.oengus.adapter.rest.dto.BooleanStatusDto;
import app.oengus.adapter.rest.dto.DataListDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.*;
import app.oengus.adapter.rest.mapper.SavedCategoryDtoMapper;
import app.oengus.adapter.rest.mapper.SavedGameDtoMapper;
import app.oengus.application.SavedGameService;
import app.oengus.application.port.security.UserSecurityPort;
import app.oengus.domain.exception.CategoryNotFoundException;
import app.oengus.domain.exception.GameNotFoundException;
import app.oengus.domain.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserSavedGamesApiController implements UserSavedGamesApi {
    private final UserSecurityPort securityPort;
    private final SavedGameDtoMapper savedGameMapper;
    private final SavedCategoryDtoMapper savedCategoryMapper;
    private final SavedGameService savedGameService;

    @Override
    public ResponseEntity<DataListDto<SavedGameDto>> getMySavedGames() {
        final var currUserId = this.securityPort.getAuthenticatedUserId();

        // Should never happen in theory, but just in case.
        if (currUserId == -1) {
            throw new UserNotFoundException();
        }

        final var savedGames = this.savedGameService.getByUserId(currUserId)
            .stream()
            .map(this.savedGameMapper::fromDomain)
            .toList();

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new DataListDto<>(savedGames));
    }

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

    @Override
    public ResponseEntity<SavedGameDto> update(int gameId, SavedGameUpdateDto body) {
        final var userId = this.securityPort.getAuthenticatedUserId();
        final var oldGame = this.savedGameService.findByIdAndUser(gameId, userId)
            .orElseThrow(GameNotFoundException::new);

        this.savedGameMapper.applyPatch(oldGame, body);

        final var savedGame = this.savedGameService.save(oldGame);
        final var savedGameDto = this.savedGameMapper.fromDomain(savedGame);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(savedGameDto);
    }

    @Override
    public ResponseEntity<SavedCategoryDto> createCategory(int gameId, SavedCategoryCreateDto body) {
        final var userId = this.securityPort.getAuthenticatedUserId();
        final var foundGame = this.savedGameService.findByIdAndUser(gameId, userId)
            .orElseThrow(GameNotFoundException::new);
        final var unsavedSavedCategory = this.savedCategoryMapper.createToDomain(gameId, body);
        final var savedCategory = this.savedGameService.saveCategory(unsavedSavedCategory);

        // foundGame.getCategories().add(savedCategory);
        // this.savedGameService.save(foundGame);

        final var dto = this.savedCategoryMapper.fromDomain(savedCategory);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(dto);
    }

    @Override
    public ResponseEntity<SavedCategoryDto> updateCategory(int gameId, int categoryId, SavedCategoryCreateDto body) {
        final var oldCategory = this.savedGameService.findCategoryByIdAndGame(gameId, categoryId)
            .orElseThrow(CategoryNotFoundException::new);

        this.savedCategoryMapper.applyPatch(oldCategory, body);

        final var savedCategory = this.savedGameService.saveCategory(oldCategory);
        final var dto = this.savedCategoryMapper.fromDomain(savedCategory);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(dto);
    }

    @Override
    public ResponseEntity<BooleanStatusDto> delete(int gameId) {
        final var userId = this.securityPort.getAuthenticatedUserId();
        final var foundGame = this.savedGameService.findByIdAndUser(gameId, userId)
            .orElseThrow(GameNotFoundException::new);

        this.savedGameService.delete(foundGame);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new BooleanStatusDto(true));
    }

    @Override
    public ResponseEntity<BooleanStatusDto> deleteCategory(int gameId, int categoryId) {
        final var foundCategory = this.savedGameService.findCategoryByIdAndGame(gameId, categoryId)
            .orElseThrow(CategoryNotFoundException::new);

        this.savedGameService.deleteCategory(foundCategory);

        return ResponseEntity.ok()
            .cacheControl(CacheControl.noCache())
            .body(new BooleanStatusDto(true));
    }
}
