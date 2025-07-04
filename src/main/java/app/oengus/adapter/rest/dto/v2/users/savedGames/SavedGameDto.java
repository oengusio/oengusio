package app.oengus.adapter.rest.dto.v2.users.savedGames;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "A game saved by a user, used for showcasing on profiles and submission autocompletion")
public record SavedGameDto(
    @Schema(description = "Unique identifier of this saved game.")
    int id,
    // TODO: add user id? Might be redundant as we already have it into the url

    @Schema(description = "The name of the game. E.G. \"The Sanley Parable\"")
    String name,

    @Schema(description = "A paragraph describing the game.")
    String description,

    @Schema(description = "The console that the game is played on")
    String console,

    @Schema(description = "The ratio or resolution of the game. E.G. 16:9, 1080x1920")
    String ratio,

    @Schema(description = "True if this game is played on an emulator.")
    boolean emulated,

    @Schema(description = "List of categories that this game has.")
    List<?> categories
) {
}
