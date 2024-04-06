package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.EmailVerification;
import app.oengus.domain.PendingEmailVerification;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, // injection strategy only needed when "uses" is filled
    uses = {
        UserMapper.class,
    }
)
public interface EmailVerificationMapper {
    PendingEmailVerification toDomain(EmailVerification model);

    EmailVerification fromDomain(PendingEmailVerification domain);
}
