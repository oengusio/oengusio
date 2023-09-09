package app.oengus.service.auth;

import app.oengus.service.UserService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder pwEncoder = new BCryptPasswordEncoder(20);

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public String encodePassword(String password) {
        return this.pwEncoder.encode(password);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        return this.pwEncoder.matches(password, encodedPassword);
    }
}
