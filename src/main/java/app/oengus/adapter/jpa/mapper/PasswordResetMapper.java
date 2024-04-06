package app.oengus.adapter.jpa.mapper;

import app.oengus.domain.PendingPasswordReset;
import app.oengus.adapter.jpa.entity.PasswordReset;
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
public interface PasswordResetMapper {
    PendingPasswordReset toDomain(PasswordReset model);

    @Mapping(source = "user.id", target = "userId")
    PasswordReset fromDomain(PendingPasswordReset domain);
}
