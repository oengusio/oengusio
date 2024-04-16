package app.oengus.adapter.rest.mapper;

import app.oengus.adapter.rest.dto.v1.SubmissionDto;
import app.oengus.domain.submission.Submission;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        GameDtoMapper.class,
    }
)
public interface SubmissionDtoMapper {
    SubmissionDto toV1Dto(Submission submission);

    Submission fromV1Dto(SubmissionDto submissionDto);
}
