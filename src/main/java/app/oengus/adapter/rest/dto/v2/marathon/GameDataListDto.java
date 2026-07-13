package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class GameDataListDto extends AbstractDataListDto<GameDto> {
    public GameDataListDto() {
        super();
    }

    public GameDataListDto(Collection<GameDto> data) {
        super(data);
    }
}
