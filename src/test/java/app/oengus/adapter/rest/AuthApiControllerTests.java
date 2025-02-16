package app.oengus.adapter.rest;

import app.oengus.adapter.rest.dto.v2.auth.LoginResponseDto;
import app.oengus.application.UserService;
import app.oengus.factory.OengusUserFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class AuthApiControllerTests {

    private final MockMvcTester mvc;
    private final OengusUserFactory userFactory;
    private final UserService userService;

    @Test
    public void testLoginForcesPasswordResetWhenRequired() {
        final var testUser = this.userFactory.getNormalUser();
        testUser.setUsername("testuser");
        testUser.setPassword(":)");
        testUser.setEmail("test@example.com");
        testUser.setNeedsPasswordReset(true);

        this.userService.save(testUser);

        assertThat(
            this.mvc.post()
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"Password123!\"}")
                .uri("/v2/auth/login")
        )
            .hasStatus(401)
            .bodyJson()
            .extractingPath("$.status")
            .asString()
            .isEqualTo(LoginResponseDto.Status.PASSWORD_RESET_REQUIRED.name());
    }

    @Test
    public void usersWithEmptyPasswordReturnInvalidPasswordWhenLoginIsTried() {
        final var testUser = this.userFactory.getNormalUser();
        testUser.setUsername("testuser2");
        testUser.setPassword("");
        testUser.setEmail("test@example.com");
        testUser.setNeedsPasswordReset(false);

        this.userService.save(testUser);

        assertThat(
            this.mvc.post()
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser2\",\"password\":\"Password123!\"}")
                .uri("/v2/auth/login")
        )
            .hasStatus(401)
            .bodyJson()
            .extractingPath("$.status")
            .asString()
            .isEqualTo(LoginResponseDto.Status.USERNAME_PASSWORD_INCORRECT.name());
    }
}
