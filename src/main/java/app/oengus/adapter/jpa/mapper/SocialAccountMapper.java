package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.Connection;
import app.oengus.adapter.jpa.entity.SocialAccount;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        // TODO?
    }
)
public interface SocialAccountMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "user", "usernameValidForPlatform" })
    Connection toDomain(SocialAccount account);

    @Mapping(target = "user", ignore = true)
    @InheritInverseConfiguration(name = "toDomain")
    SocialAccount fromDomain(Connection connection);
}
