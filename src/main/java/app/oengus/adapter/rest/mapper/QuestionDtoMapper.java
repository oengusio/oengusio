package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.V1QuestionDto;
import app.oengus.domain.marathon.Question;
import app.oengus.domain.marathon.QuestionType;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ValueMapping;

@Mapper(componentModel = "spring")
public interface QuestionDtoMapper {
    @Mapping(target = "type", source = "questionType")
    Question fromV1Dto(V1QuestionDto dto);

    @InheritInverseConfiguration(name = "fromV1Dto")
    V1QuestionDto toV1Dto(Question question);

    @ValueMapping(target = "SUBMISSION", source = "SUBMISSION")
    @ValueMapping(target = "DONATION", source = "DONATION")
    QuestionType typeFromString(String type);

    default String typeToString(QuestionType type) {
        return type.name();
    }
}
