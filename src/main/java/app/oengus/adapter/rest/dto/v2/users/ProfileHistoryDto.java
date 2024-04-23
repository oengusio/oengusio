package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.v2.simple.SimpleGameDto;
import app.oengus.adapter.rest.dto.v2.simple.SimpleOpponentDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Schema
public class ProfileHistoryDto extends ModeratedHistoryDto {

    private List<SimpleGameDto> games = new ArrayList<>();

    // TODO: do we need this?
    //  Doesn't seem like it, very useless info currently.
    //  Just id + video url
    // TODO: remove this, it is no longer mapped
    private List<SimpleOpponentDto> opponents = new ArrayList<>();
}
