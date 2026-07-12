package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

public class ScheduleInfoDataListDto extends DataListDto<ScheduleInfoDto> {
    public ScheduleInfoDataListDto() {
        super();
    }

    public ScheduleInfoDataListDto(Collection<ScheduleInfoDto> data) {
        super(data);
    }
}
