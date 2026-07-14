package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.adapter.rest.dto.AbstractDataListDto;

import java.util.Collection;

public class QuestionDataListDto extends AbstractDataListDto<QuestionDto> {
    public QuestionDataListDto() {
        super();
    }

    public QuestionDataListDto(Collection<QuestionDto> data) {
        super(data);
    }
}
