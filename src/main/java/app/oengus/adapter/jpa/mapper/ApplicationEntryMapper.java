package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.ApplicationEntry;
import app.oengus.domain.volunteering.Application;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    uses = {
        //
    }
)
public interface ApplicationEntryMapper {
    ApplicationEntry fromDomain(Application application);

    Application toDomain(ApplicationEntry applicationEntry);
}
