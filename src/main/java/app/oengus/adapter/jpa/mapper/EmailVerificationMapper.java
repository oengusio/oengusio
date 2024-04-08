package app.oengus.adapter.jpa.mapper;

import app.oengus.adapter.jpa.entity.EmailVerification;
import app.oengus.domain.PendingEmailVerification;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR, // injection strategy only needed when "uses" is filled
    uses = {
        UserMapper.class,
    }
)
public interface EmailVerificationMapper {
    PendingEmailVerification toDomain(EmailVerification model);

    @Mapping(source = "user.id", target = "userId")
    EmailVerification fromDomain(PendingEmailVerification domain);
}