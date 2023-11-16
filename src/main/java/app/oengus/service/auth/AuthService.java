package app.oengus.service.auth;

import app.oengus.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder pwEncoder = new BCryptPasswordEncoder(10);

    public String encodePassword(String password) {
        return this.pwEncoder.encode(password);
    }

    public boolean validatePassword(String password, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }

        return this.pwEncoder.matches(password, encodedPassword);
    }
}
