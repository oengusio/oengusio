package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.submission.Answer;
import app.oengus.adapter.jpa.entity.AnswerEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface AnswerEntityMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "answerRequired" })
    @Mapping(target = "questionId", source = "question.id")
    @Mapping(target = "question", source = "question")
    @Mapping(target = "submissionId", source = "submission.id")
    Answer toDomain(AnswerEntity answerEntity);

    @InheritInverseConfiguration(name = "toDomain")
    AnswerEntity fromDomain(Answer answer);
}
