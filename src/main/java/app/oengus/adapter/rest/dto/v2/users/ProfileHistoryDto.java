package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.v2.simple.SimpleGameDto;
import app.oengus.adapter.rest.dto.v2.simple.SimpleOpponentDto;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;

@Schema
public class ProfileHistoryDto extends ModeratedHistoryDto {

    @JsonView(Views.Public.class)
    private List<SimpleGameDto> games = new ArrayList<>();

    // TODO: do we need this?
    private List<SimpleOpponentDto> opponents = new ArrayList<>();

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
