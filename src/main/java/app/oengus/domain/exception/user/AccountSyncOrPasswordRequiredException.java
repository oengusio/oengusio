package app.oengus.domain.exception.user;

public class AccountSyncOrPasswordRequiredException extends RuntimeException {
    public AccountSyncOrPasswordRequiredException() {
        super("You must either have a password set, or link your account to a social provider");
    }
}
