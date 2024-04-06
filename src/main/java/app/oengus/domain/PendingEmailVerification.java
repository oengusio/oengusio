package app.oengus.domain;

public record PendingEmailVerification(
    OengusUser user,
    String verificationHash
) {
}
