package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.ApplicationAuditlog;
import app.oengus.adapter.jpa.entity.ApplicationEntry;
import app.oengus.domain.volunteering.Application;
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
public interface ApplicationEntryMapper {
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "teamId", source = "team.id")
    Application toDomain(ApplicationEntry applicationEntry);

    @InheritInverseConfiguration(name = "toDomain")
    ApplicationEntry fromDomain(Application application);

    @BeanMapping(ignoreUnmappedSourceProperties = { "application" })
    @Mapping(target = "userId", source = "user.id")
    Application.AuditLog toDomain(ApplicationAuditlog appLog);

    @Mapping(target = "application", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    ApplicationAuditlog fromDomain(Application.AuditLog domainLog);
}
