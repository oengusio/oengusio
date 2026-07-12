package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

public class LineDataListDto extends DataListDto<LineDto> {
    public LineDataListDto() {
        super();
    }

    public LineDataListDto(Collection<LineDto> data) {
        super(data);
    }
}
