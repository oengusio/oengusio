package app.oengus.adapter.rest.dto.v2.users.savedGames;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

public class SavedGameDataListDto extends DataListDto<SavedGameDto> {
    public SavedGameDataListDto() {
        super();
    }

    public SavedGameDataListDto(Collection<SavedGameDto> data) {
        super(data);
    }
}
