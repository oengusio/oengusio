package app.oengus.entity.dto.v2.users;

import app.oengus.entity.dto.v2.SimpleGameDto;
import app.oengus.entity.dto.v2.SimpleOpponentDto;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Schema
public class ProfileHistory {

    // TODO: marathon -> games -> categories
    @JsonView(Views.Public.class)
    @Schema(description = "The id of the marathon this run was submitted to")
    private String marathonId;

    @JsonView(Views.Public.class)
    @Schema(description = "The name of the marathon this run was submitted to")
    private String marathonName;

    @JsonView(Views.Public.class)
    @Schema(description = "The start date of the marathon this run was submitted to")
    private ZonedDateTime marathonStartDate;

    @JsonView(Views.Public.class)
    private List<SimpleGameDto> games = new ArrayList<>();

    // TODO: do we need this?
    private List<SimpleOpponentDto> opponents = new ArrayList<>();

    public String getMarathonId() {
        return marathonId;
    }

    public void setMarathonId(String marathonId) {
        this.marathonId = marathonId;
    }

    public String getMarathonName() {
        return marathonName;
    }

    public void setMarathonName(String marathonName) {
        this.marathonName = marathonName;
    }

    public ZonedDateTime getMarathonStartDate() {
        return marathonStartDate;
    }

    public void setMarathonStartDate(ZonedDateTime marathonStartDate) {
        this.marathonStartDate = marathonStartDate;
    }

    public List<SimpleGameDto> getGames() {
        return games;
    }

    public void setGames(List<SimpleGameDto> games) {
        this.games = games;
    }

    public List<SimpleOpponentDto> getOpponents() {
        return opponents;
    }

    public void setOpponents(List<SimpleOpponentDto> opponents) {
        this.opponents = opponents;
    }
}
