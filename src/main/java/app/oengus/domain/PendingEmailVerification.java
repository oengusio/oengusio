package app.oengus.domain;

import java.time.LocalDate;

public record PendingEmailVerification(
    OengusUser user,
    String verificationHash,
    LocalDate createdAt
) {
}
