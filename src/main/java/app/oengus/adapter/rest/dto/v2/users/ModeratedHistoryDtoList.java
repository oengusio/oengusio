package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

// Concrete DataListDto<ModeratedHistoryDto> so springdoc can resolve it into a named schema.
public class ModeratedHistoryDtoList extends DataListDto<ModeratedHistoryDto> {
    public ModeratedHistoryDtoList() {
        super();
    }

    public ModeratedHistoryDtoList(Collection<ModeratedHistoryDto> data) {
        super(data);
    }
}
