package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

public class ProfileHistoryDataListDto extends DataListDto<ProfileHistoryDto> {
    public ProfileHistoryDataListDto() {
        super();
    }

    public ProfileHistoryDataListDto(Collection<ProfileHistoryDto> data) {
        super(data);
    }
}
