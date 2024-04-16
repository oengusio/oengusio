package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.EmailVerification;
import app.oengus.domain.PendingEmailVerification;
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
public interface EmailVerificationMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = { "userId" })
    PendingEmailVerification toDomain(EmailVerification model);

    @Mapping(target = "userId", source = "user.id")
    @InheritInverseConfiguration(name = "toDomain")
    EmailVerification fromDomain(PendingEmailVerification domain);
}
