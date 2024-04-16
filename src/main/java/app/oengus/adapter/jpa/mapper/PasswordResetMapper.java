package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.PendingPasswordReset;
import app.oengus.adapter.jpa.entity.PasswordReset;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, // injection strategy only needed when "uses" is filled
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    uses = {
        UserMapper.class,
    }
)
public interface PasswordResetMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "userId" })
    PendingPasswordReset toDomain(PasswordReset model);

    @Mapping(source = "user.id", target = "userId")
    PasswordReset fromDomain(PendingPasswordReset domain);
}
