package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

// Concrete DataListDto<ProfileHistoryDto> so springdoc can resolve it into a named schema.
public class ProfileHistoryDtoList extends DataListDto<ProfileHistoryDto> {
    public ProfileHistoryDtoList() {
        super();
    }

    public ProfileHistoryDtoList(Collection<ProfileHistoryDto> data) {
        super(data);
    }
}
