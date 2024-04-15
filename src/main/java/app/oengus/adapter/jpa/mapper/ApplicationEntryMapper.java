package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.ApplicationEntry;
import app.oengus.domain.volunteering.Application;
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
public interface ApplicationEntryMapper {
    ApplicationEntry fromDomain(Application application);

    Application toDomain(ApplicationEntry applicationEntry);
}
