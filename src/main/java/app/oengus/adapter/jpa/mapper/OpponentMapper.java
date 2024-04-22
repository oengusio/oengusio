package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.OpponentEntity;
import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.domain.submission.Opponent;
import app.oengus.domain.submission.Submission;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        CategoryMapper.class,
    }
)
public interface OpponentMapper {
    @Mapping(target = "categoryId", source = "category.id")
    Opponent toDomain(OpponentEntity entity);

    @InheritInverseConfiguration(name = "toDomain")
    OpponentEntity fromDomain(Opponent opponent);

    default Submission toDomain(SubmissionEntity entity) {
        var mapper = Mappers.getMapper( SubmissionEntityMapper.class );

        return mapper.toDomain(entity);
    }

    default SubmissionEntity fromDomain(Submission submission) {
        var mapper = Mappers.getMapper( SubmissionEntityMapper.class );

        return mapper.fromDomain(submission);
    }
}
