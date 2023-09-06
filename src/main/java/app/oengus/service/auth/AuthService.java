package app.oengus.service.auth;

import app.oengus.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }
}
