package app.oengus.adapter.rest.dto.v2.schedule;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

// Concrete DataListDto<LineDto> so springdoc can resolve it into a named schema.
public class LineDtoList extends DataListDto<LineDto> {
    public LineDtoList() {
        super();
    }

    public LineDtoList(Collection<LineDto> data) {
        super(data);
    }
}
