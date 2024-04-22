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
        OpponentMapper.class,
    }
)
public interface SubmissionEntityMapper {
    @Mapping(target = "marathonId", source = "marathon.id")
    Submission toDomain(SubmissionEntity submissionEntity);

    // TODO: fix
    @Mapping(target = "fresh", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    SubmissionEntity fromDomain(Submission submission);
}
