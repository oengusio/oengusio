package app.oengus.entity.dto.v2.marathon;

import app.oengus.entity.dto.OpponentCategoryDto;
import app.oengus.entity.model.RunType;

import java.time.Duration;
import java.util.List;

public class CategoryDto {
    private int id;
    private String name;
    private Duration Estimate;
    private String description;
    private String video;
    // TODO: include code as well?
    private RunType type;
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
        return Estimate;
    }

    public void setEstimate(Duration estimate) {
        Estimate = estimate;
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

    public List<OpponentCategoryDto> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<OpponentCategoryDto> opponents) {
        this.opponents = opponents;
    }
}
