package app.oengus.spring;

import app.oengus.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class RandomTestClass {
    private final EmailService emailService;

    @PostConstruct
    public void doTheTestThing() {
        this.emailService.sendTestEmail(
            "duncte123@oengus.io",
            "contact@duncte123.me"
        );
    }
}
