package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.AnswerDto;
import app.oengus.adapter.rest.dto.v1.V1AnswerDto;
import app.oengus.domain.submission.Answer;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        QuestionDtoMapper.class,
    }
)
public interface AnswerDtoMapper {
    @Mapping(target = "username", source = "user.username")
    V1AnswerDto v1fromDomain(Answer answer);

    Answer fromV1Dto(V1AnswerDto v1AnswerDto);

    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "username", source = "user.username")
    AnswerDto fromDomain(Answer answer);
}
