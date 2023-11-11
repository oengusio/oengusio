package app.oengus.spring;

import app.oengus.entity.model.User;
import app.oengus.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class RandomTestClass {
    private final EmailService emailService;

    @PostConstruct
    public void doTheTestThing() throws Exception {
        final User fakeUser = new User();

        fakeUser.setMail("duncte123@oengus.io");
        fakeUser.setUsername("duncte123");
        fakeUser.setDisplayName("Duncte");

        this.emailService.sendEmailVerification(
            fakeUser,
            "AAAAAAAAAa",
            "oengus.io"
        );
    }
}
