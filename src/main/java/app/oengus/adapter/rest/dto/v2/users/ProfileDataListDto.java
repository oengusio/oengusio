package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class ProfileDataListDto extends AbstractDataListDto<ProfileDto> {
    public ProfileDataListDto() {
        super();
    }

    public ProfileDataListDto(Collection<ProfileDto> data) {
        super(data);
    }
}
