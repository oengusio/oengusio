package app.oengus.adapter.rest.dto.v2.users.savedGames;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Duration;

@Schema(description = "Category that is attached to a saved game.")
public record SavedCategoryDto(
    @Schema(description = "The unique id of this category, -1 in case of a new one")
    int id,

    @Schema(description = "Id of the game this is connected to, irrelevant for api usage")
    int gameId,

    @Schema(description = "The name of this category. E.G. Any%")
    String name,

    @Schema(description = "A paragraph detailing what this category is about")
    String description,

    @Schema(
        description = "Approximate duration of the run. Formatted in the ISO-8601 duration format.",
        example = "PT5H30M20S"
    )
    Duration estimate,

    @Schema(description = "Showcase video of this category.")
    String video
) {
}
