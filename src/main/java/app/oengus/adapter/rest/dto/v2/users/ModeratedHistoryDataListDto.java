package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

public class ModeratedHistoryDataListDto extends DataListDto<ModeratedHistoryDto> {
    public ModeratedHistoryDataListDto() {
        super();
    }

    public ModeratedHistoryDataListDto(Collection<ModeratedHistoryDto> data) {
        super(data);
    }
}
