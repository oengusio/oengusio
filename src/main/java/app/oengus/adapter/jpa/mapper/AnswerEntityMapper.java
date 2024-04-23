package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.submission.Answer;
import app.oengus.adapter.jpa.entity.AnswerEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        QuestionMapper.class,
        UserMapper.class,
    }
)
public interface AnswerEntityMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "answerRequired" })
    @Mapping(target = "submissionId", source = "submission.id")
    @Mapping(target = "user", source = "submission.user")
    Answer toDomain(AnswerEntity answerEntity);

    @InheritInverseConfiguration(name = "toDomain")
    AnswerEntity fromDomain(Answer answer);
}
