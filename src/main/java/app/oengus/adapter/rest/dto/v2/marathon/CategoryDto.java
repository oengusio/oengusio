package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.entity.model.Category;
import app.oengus.entity.model.Opponent;
import app.oengus.entity.model.RunType;

import java.time.Duration;
import java.util.List;

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Duration getEstimate() {
        return estimate;
    }

    public void setEstimate(Duration estimate) {
        this.estimate = estimate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public RunType getType() {
        return type;
    }

    public void setType(RunType type) {
        this.type = type;
    }

    public int getGameId() {
        return gameId;
    }

    public void setGameId(int gameId) {
        this.gameId = gameId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<OpponentCategoryDto> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<OpponentCategoryDto> opponents) {
        this.opponents = opponents;
    }

    public static CategoryDto fromCategory(Category category) {
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
