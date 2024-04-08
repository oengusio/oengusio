package app.oengus.adapter.rest.controller.v1;

import app.oengus.adapter.rest.dto.v1.OpponentSubmissionDto;
import app.oengus.adapter.rest.mapper.UserDtoMapper;
import app.oengus.application.CategoryService;
import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.application.port.persistence.UserPersistencePort;
import app.oengus.domain.Game;
import app.oengus.domain.OengusUser;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Operation(summary = "Find a multiplayer category by its code and return basic information"/*,
        response = OpponentSubmissionDto.class*/)
    public ResponseEntity<OpponentSubmissionDto> findCategoryByCode(@PathVariable("marathonId") final String marathonId,
                                                                    @PathVariable("code") final String code) {
        final var pair = this.categoryService.findCategoryByCode(marathonId, code);
        final var category = pair.getLeft();
        final var userIds = pair.getRight();
        final List<OengusUser> users = this.userPersistencePort.findAllById(userIds);
        final OpponentSubmissionDto opponentDto = new OpponentSubmissionDto();
        final Game game = this.gamePersistencePort.findById(category.getGameId()).get();

        opponentDto.setUsers(
            users.stream()
                .map(this.userMapper::fromDomain)
                .toList()
        );
        opponentDto.setGameName(game.getName());
        opponentDto.setCategoryName(category.getName());
        opponentDto.setCategoryId(category.getId());

        return ResponseEntity.ok(opponentDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("canUpdateMarathon(#marathonId) && !isBanned() || isAdmin()")
    @Operation(hidden = true)
    public ResponseEntity<?> delete(@PathVariable("marathonId") final String marathonId,
                                    @PathVariable("id") final int id) throws NotFoundException {
        this.categoryService.delete(marathonId, id);

        return ResponseEntity.ok().build();
    }

}
