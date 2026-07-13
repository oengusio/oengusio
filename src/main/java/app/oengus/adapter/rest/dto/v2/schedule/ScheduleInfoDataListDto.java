package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class ScheduleInfoDataListDto extends AbstractDataListDto<ScheduleInfoDto> {
    public ScheduleInfoDataListDto() {
        super();
    }

    public ScheduleInfoDataListDto(Collection<ScheduleInfoDto> data) {
        super(data);
    }
}
