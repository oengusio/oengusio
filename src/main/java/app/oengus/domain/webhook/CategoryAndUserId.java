package app.oengus.domain.webhook;

import app.oengus.domain.submission.Category;

public record CategoryAndUserId(Category category, int userId) {
}
