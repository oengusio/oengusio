package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class LineDataListDto extends AbstractDataListDto<LineDto> {
    public LineDataListDto() {
        super();
    }

    public LineDataListDto(Collection<LineDto> data) {
        super(data);
    }
}
