package app.oengus.adapter.rest.dto.v2.users;

import app.oengus.adapter.rest.dto.AbstractDataListDto;
import app.oengus.domain.Role;

import java.util.Collection;

public class RoleDataListDto extends AbstractDataListDto<Role> {
    public RoleDataListDto() {
        super();
    }

    public RoleDataListDto(Collection<Role> data) {
        super(data);
    }
}
