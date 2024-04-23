package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.QuestionEntity;
import app.oengus.domain.marathon.Question;
import app.oengus.domain.marathon.QuestionType;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        //
    }
)
public interface QuestionMapper {
    @Mapping(target = "type", source = "questionType")
    @Mapping(target = "marathonId", source = "marathon.id")
    Question toDomain(QuestionEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    QuestionEntity fromDomain(Question question);

    @ValueMapping(target = "SUBMISSION", source = "SUBMISSION")
    @ValueMapping(target = "DONATION", source = "DONATION")
    QuestionType typeFromString(String type);

    default String typeToString(QuestionType type) {
        return type.name();
    }
}
