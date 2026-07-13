package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class SubmissionDataListDto extends AbstractDataListDto<SubmissionDto> {
    public SubmissionDataListDto() {
        super();
    }

    public SubmissionDataListDto(Collection<SubmissionDto> data) {
        super(data);
    }
}
