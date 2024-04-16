package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Submission;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        GameMapper.class,
    }
)
public interface SubmissionEntityMapper {
    // TODO: implement these when needed
    @BeanMapping(ignoreUnmappedSourceProperties = { "csvHeaders", "opponentDtos", "opponents", "answers" })
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "marathon.id", target = "marathonId")
    Submission toDomain(SubmissionEntity submissionEntity);

    // TODO: fix
    @Mapping(target = "fresh", ignore = true)
    @Mapping(target = "answers", ignore = true)
    @Mapping(target = "opponents", ignore = true)
    @Mapping(target = "opponentDtos", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    SubmissionEntity fromDomain(Submission submission);
}
