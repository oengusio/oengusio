package app.oengus.domain;

import java.time.LocalDate;

public record PendingPasswordReset(
    OengusUser user,
    String token,
    LocalDate createdAt
) {
}
