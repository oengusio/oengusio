package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Submission;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        // TODO: availability mapper
    }
)
public interface SubmissionEntityMapper {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "marathon.id", target = "marathonId")
    Submission toDomain(SubmissionEntity submissionEntity);

    @InheritInverseConfiguration(name = "toDomain")
    SubmissionEntity fromDomain(Submission submission);
}
