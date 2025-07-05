package app.oengus.adapter.rest.dto.v2.users.savedGames;

import app.oengus.domain.submission.Game;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SavedGameCreateDto(
    @NotBlank
    @Size(max = Game.NAME_MAX_LENGTH)
    @Schema(description = "The name of the game. E.G. \"The Sanley Parable\"")
    String name,

    @NotBlank
    @Size(max = Game.DESCRIPTION_MAX_LENGTH)
    @Schema(description = "A paragraph describing the game.")
    String description,

    @NotBlank
    @Size(max = Game.CONSOLE_MAX_LENGTH)
    @Schema(description = "The console that the game is played on")
    String console,

    @NotBlank
    @Size(max = Game.RATIO_MAX_LENGTH)
    @Schema(description = "The ratio or resolution of the game. E.G. 16:9, 1080x1920")
    String ratio,

    @NotNull
    @Schema(description = "True if this game is played on an emulator.")
    boolean emulated,

    @NotNull
    @Size(max = 20) // TODO: what would be a good number to limit this at? Like no sane person learns 20 categories in a single game right?
    @Schema(description = "List of categories that this game has.")
    List<SavedCategoryCreateDto> categories
) {
}
