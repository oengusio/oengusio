package app.oengus.adapter.rest.dto.v2.marathon;

import app.oengus.adapter.rest.dto.DataListDto;

import java.util.Collection;

// Concrete DataListDto<QuestionDto> so springdoc can resolve it into a named schema.
public class QuestionDtoList extends DataListDto<QuestionDto> {
    public QuestionDtoList() {
        super();
    }

    public QuestionDtoList(Collection<QuestionDto> data) {
        super(data);
    }
}
