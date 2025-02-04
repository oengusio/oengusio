package app.oengus.application.webhook.dto;

import app.oengus.domain.SocialPlatform;

public record WebhookConnectionDto(SocialPlatform platform, String username) {
}
