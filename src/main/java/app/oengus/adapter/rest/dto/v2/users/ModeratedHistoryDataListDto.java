package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class ModeratedHistoryDataListDto extends AbstractDataListDto<ModeratedHistoryDto> {
    public ModeratedHistoryDataListDto() {
        super();
    }

    public ModeratedHistoryDataListDto(Collection<ModeratedHistoryDto> data) {
        super(data);
    }
}
