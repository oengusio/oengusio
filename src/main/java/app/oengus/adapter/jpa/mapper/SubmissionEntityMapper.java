package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.domain.submission.Submission;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        UserMapper.class,
        GameMapper.class,
        AnswerEntityMapper.class,
        AvailabilityMapper.class,
    }
)
public interface SubmissionEntityMapper {
    // TODO: implement these when needed
    @BeanMapping(ignoreUnmappedSourceProperties = {  "opponents" })
//    @Mapping(source = "user.id", target = "userId")
    @Mapping(target = "marathonId", source = "marathon.id")
    @Mapping(target = "opponents", ignore = true)
    Submission toDomain(SubmissionEntity submissionEntity);

    // TODO: fix
    @BeanMapping(ignoreUnmappedSourceProperties = { "opponents" })
    @Mapping(target = "fresh", ignore = true)
    @Mapping(target = "opponents", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    SubmissionEntity fromDomain(Submission submission);
}
