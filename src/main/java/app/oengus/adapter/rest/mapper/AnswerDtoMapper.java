package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.AnswerDto;
import app.oengus.adapter.rest.dto.v1.V1AnswerDto;
import app.oengus.domain.submission.Answer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AnswerDtoMapper {
    V1AnswerDto v1fromDomain(Answer answer);

    AnswerDto fromDomain(Answer answer);
}
