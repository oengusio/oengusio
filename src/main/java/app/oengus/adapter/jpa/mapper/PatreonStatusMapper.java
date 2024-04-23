package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.PatreonStatus;
import app.oengus.domain.PledgeInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        //
    }
)
public interface PatreonStatusMapper {
    PledgeInfo toDomain(PatreonStatus entity);

    PatreonStatus fromDomain(PledgeInfo pledgeInfo);
}
