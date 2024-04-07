package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.entity.model.CategoryEntity;
import app.oengus.entity.model.Opponent;
import app.oengus.domain.RunType;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
public class CategoryDto {
    private int id;
    private String name;
    private Duration estimate;
    private String description;
    private String video;
    // TODO: include code as well?
    private RunType type;
    private int gameId;
    private int userId;
    private List<OpponentCategoryDto> opponents;

    @Deprecated(forRemoval = true)
    public static CategoryDto fromCategory(CategoryEntity category) {
        final CategoryDto dto = new CategoryDto();

        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setEstimate(category.getEstimate());
        dto.setDescription(category.getDescription());
        dto.setVideo(category.getVideo());
        dto.setType(category.getType());
        dto.setGameId(category.getGame().getId());
        dto.setUserId(category.getGame().getSubmission().getUser().getId());

        final List<Opponent> opponents = category.getOpponents();

        if (opponents == null) {
            dto.setOpponents(List.of());
        } else {
            dto.setOpponents(
                opponents.stream().map(OpponentCategoryDto::fromOpponent).toList()
            );
        }


        return dto;
    }
}
