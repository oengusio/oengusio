package app.oengus.requests.user;

public interface IUserRequest {
    static final String USERNAME_REGEX = "^[\\w\\-]{3,32}$";
    static final String SPEEDRUN_COM_NAME_REGEX = "^[\\w\\.\\-À-Üà-øoù-ÿŒœ]{0,20}$";
}
