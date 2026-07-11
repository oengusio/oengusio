package app.oengus.adapter.rest.dto.v2.users.savedGames;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

// Concrete DataListDto<SavedGameDto> so springdoc can resolve it into a named schema.
public class SavedGameDtoList extends DataListDto<SavedGameDto> {
    public SavedGameDtoList() {
        super();
    }

    public SavedGameDtoList(Collection<SavedGameDto> data) {
        super(data);
    }
}
