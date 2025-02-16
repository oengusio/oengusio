package app.oengus.application.webhook.mapper;

import app.oengus.application.webhook.dto.WebhookConnectionDto;
import app.oengus.application.webhook.dto.WebhookSubmissionDto;
import app.oengus.application.webhook.dto.WebhookUserDto;
import app.oengus.domain.Connection;
import app.oengus.domain.OengusUser;
import app.oengus.domain.submission.Submission;
import org.mapstruct.*;

@Mapper(
    componentModel = "spring",
    injectionStrategy = InjectionStrategy.CONSTRUCTOR,
    unmappedSourcePolicy = ReportingPolicy.ERROR,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface WebhookDtoMapper {
    @BeanMapping(ignoreUnmappedSourceProperties = {"answers", "availabilities"})
    WebhookSubmissionDto fromDomain(Submission submission);

    @BeanMapping(ignoreUnmappedSourceProperties = {"id"})
    WebhookConnectionDto fromDomain(Connection connection);

    @BeanMapping(ignoreUnmappedSourceProperties = {"email", "password", "enabled", "roles", "emailVerified", "mfaEnabled", "mfaSecret", "patreonId", "discordId", "twitchId", "twitterId", "createdAt", "lastLogin", "needsPasswordReset"})
    WebhookUserDto fromDomain(OengusUser user);
}
