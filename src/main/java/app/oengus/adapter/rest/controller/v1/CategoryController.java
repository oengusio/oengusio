package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.Views;
import app.oengus.adapter.rest.dto.v1.OpponentSubmissionDto;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.CategoryService;
import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.submission.Game;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static app.oengus.adapter.rest.helper.HeaderHelpers.cachingHeaders;

@RestController
@RequestMapping("/v1/marathons/{marathonId}/categories")
@Tag(name = "categories-v1")
@RequiredArgsConstructor
public class CategoryController {
    private final UserDtoMapper userMapper;
    private final CategoryService categoryService;
    private final GamePersistencePort gamePersistencePort;
    private final UserPersistencePort userPersistencePort;

    @GetMapping("/{code}")
    @JsonView(Views.Public.class)
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Find a multiplayer category by its code and return basic information")
    public ResponseEntity<OpponentSubmissionDto> findCategoryByCode(
        @PathVariable("marathonId") final String marathonId, @PathVariable("code") final String code
    ) {
        final var pair = this.categoryService.findCategoryByCode(marathonId, code);

        System.out.println("==========================================");
        System.out.println("RETURNED FROM SERVICE");
        System.out.println(pair);
        System.out.println("==========================================");

        final var category = pair.getLeft();
        final var users = pair.getRight();
        final var opponentDto = new OpponentSubmissionDto();
        // technically we are allowed to use the port here, but it is still too much logic for a controller
        final Game game = this.gamePersistencePort.findById(category.getGameId()).get();

        System.out.println("==========================================");
        System.out.println("GAME FETCHED FROM PORT");
        System.out.println(game);
        System.out.println("==========================================");

        opponentDto.setUsers(
            users.stream()
                .map(this.userMapper::fromDomain)
                .toList()
        );
        opponentDto.setGameName(game.getName());
        opponentDto.setCategoryName(category.getName());
        opponentDto.setCategoryId(category.getId());

        return ResponseEntity.ok()
            .headers(cachingHeaders(10, true))
            .body(opponentDto);
    }

    @DeleteMapping("/{id}")
//    @PreAuthorize("(!isBanned() && canUpdateMarathon(#marathonId)) || isAdmin()")
    // canUpdateMarathon does an isAdmin check for us
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("marathonId") final String marathonId,
                                    @PathVariable("id") final int id) throws NotFoundException {
        this.categoryService.delete(marathonId, id);

        return ResponseEntity.ok().build();
    }

}
