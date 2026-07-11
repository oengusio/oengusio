package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

// Concrete DataListDto<ScheduleInfoDto> so springdoc can resolve it into a named schema.
public class ScheduleInfoDtoList extends DataListDto<ScheduleInfoDto> {
    public ScheduleInfoDtoList() {
        super();
    }

    public ScheduleInfoDtoList(Collection<ScheduleInfoDto> data) {
        super(data);
    }
}
