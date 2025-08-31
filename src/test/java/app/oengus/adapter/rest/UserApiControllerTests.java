package app.oengus.adapter.rest;

import app.oengus.application.UserService;
import app.oengus.application.port.security.JWTPort;
import app.oengus.domain.OengusUser;
import app.oengus.factory.OengusUserFactory;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class UserApiControllerTests {
    private final MockMvcTester mvc;
    private final OengusUserFactory userFactory;
    private final UserService userService;
    private final JWTPort jwtPort;

    @Test
    public void authenticatedUserCanFetchOwnInfo() {
        final var user = this.createTestUser();
        final var authToken = this.jwtPort.generateToken(user);

        final var bodyJson = assertThat(
            this.mvc.get()
                .header("Accept", "application/json")
                .uri("/v2/users/@me")
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        bodyJson.extractingPath("$.id")
            .asNumber()
            .isEqualTo(user.getId());

        bodyJson.extractingPath("$.username")
            .asString()
            .isEqualTo(user.getUsername());

        bodyJson.extractingPath("$.displayName")
            .asString()
            .isEqualTo(user.getDisplayName());

        bodyJson.extractingPath("$.email")
            .asString()
            .isEqualTo(user.getEmail());

        bodyJson.extractingPath("$.enabled")
            .asBoolean()
            .isEqualTo(true);

        bodyJson.extractingPath("$.country")
            .asString()
            .isEqualTo(user.getCountry());
    }

    @Test
    public void authenticatedUserCanUpdateSelf() {
        final var user = this.createTestUser();
        final var authToken = this.jwtPort.generateToken(user);

        String patchBody = """
            {"username": "%s", "displayName": "%s", "email": "coolNewEmail@example.com", "enabled": true, "pronouns": [], "languagesSpoken": [], "country": "NL", "connections": [], "discordId": "1234567890", "twitchId": null, "patreonId": null}
            """.trim().formatted(user.getUsername().toUpperCase(Locale.ROOT), user.getDisplayName());

        final var bodyJson = assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        bodyJson.extractingPath("$.username")
            .asString()
            .isEqualTo(user.getUsername());

        bodyJson.extractingPath("$.displayName")
            .asString()
            .isEqualTo(user.getDisplayName());

        bodyJson.extractingPath("$.email")
            .asString()
            .isEqualTo("coolNewEmail@example.com");

        bodyJson.extractingPath("$.emailVerified")
            .asBoolean()
            .isEqualTo(false);
    }

    @Test
    public void authenticatedUserCanUpdateSelfWithoutSocialsWhenPwSet() {
        var user = this.createTestUser();

        user.setDiscordId(null);
        user.setPassword("ASecurePassword!");

        user = this.userService.save(user);

        final var authToken = this.jwtPort.generateToken(user);

        String patchBody = """
            {"username": "%s", "displayName": "%s", "email": "coolNewEmail@example.com", "enabled": true, "pronouns": [], "languagesSpoken": [], "country": "NL", "connections": [], "discordId": null, "twitchId": null, "patreonId": null}
            """.trim().formatted(user.getUsername().toUpperCase(Locale.ROOT), user.getDisplayName());

        final var bodyJson = assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        bodyJson.extractingPath("$.username")
            .asString()
            .isEqualTo(user.getUsername());

        bodyJson.extractingPath("$.displayName")
            .asString()
            .isEqualTo(user.getDisplayName());

        bodyJson.extractingPath("$.email")
            .asString()
            .isEqualTo("coolNewEmail@example.com");

        bodyJson.extractingPath("$.emailVerified")
            .asBoolean()
            .isEqualTo(false);
    }

    @Test
    public void authenticatedUserCanUpdateSelfWithSocialsWhithoutPwSet() {
        var user = this.createTestUser();

        user.setDiscordId("1234567890");
        user.setPassword(null);

        user = this.userService.save(user);

        final var authToken = this.jwtPort.generateToken(user);

        String patchBody = """
            {"username": "%s", "displayName": "%s", "email": "coolNewEmail@example.com", "enabled": true, "pronouns": [], "languagesSpoken": [], "country": "NL", "connections": [], "discordId": "1234567890", "twitchId": null, "patreonId": null}
            """.trim().formatted(user.getUsername().toUpperCase(Locale.ROOT), user.getDisplayName());

        final var bodyJson = assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        bodyJson.extractingPath("$.username")
            .asString()
            .isEqualTo(user.getUsername());

        bodyJson.extractingPath("$.displayName")
            .asString()
            .isEqualTo(user.getDisplayName());

        bodyJson.extractingPath("$.email")
            .asString()
            .isEqualTo("coolNewEmail@example.com");

        bodyJson.extractingPath("$.emailVerified")
            .asBoolean()
            .isEqualTo(false);
    }

    @Test
    public void authenticatedUserCannotUpdateOthers() {
        final var user = this.createTestUser();
        final var authToken = this.jwtPort.generateToken(user);

        final var userToUpdate = this.createTestUser();

        String patchBody = """
            {"username": "%s", "displayName": "%s", "email": "coolNewEmail@example.com", "enabled": true, "pronouns": [], "languagesSpoken": [], "country": "NL", "connections": [], "discordId": "1234567890", "twitchId": null, "patreonId": null}
            """.trim().formatted(user.getUsername(), user.getDisplayName());

        assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/" + userToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(401);
    }

    @Test
    public void adminUserCanUpdateOthers() {
        final var user = this.createAdminUser();
        final var authToken = this.jwtPort.generateToken(user);

        final var userToUpdate = this.createTestUser();

        // Duh, I try to set the user's name to the admin's username. That is a duped username and is not allowed
        String patchBody = """
            {"username": "%s_child", "displayName": "%s", "email": "%s", "enabled": true, "pronouns": [], "languagesSpoken": [], "country": "NL", "connections": [], "discordId": "1234567890", "twitchId": null, "patreonId": null}
            """.trim().formatted(user.getUsername(), user.getDisplayName(), userToUpdate.getEmail());

        final var bodyJson = assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/" + userToUpdate.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        bodyJson.extractingPath("$.username")
            .asString()
            .isEqualTo(user.getUsername().toLowerCase(Locale.ROOT) + "_child");

        bodyJson.extractingPath("$.displayName")
            .asString()
            .isEqualTo(user.getDisplayName());
    }

    private OengusUser createTestUser() {
        final var testUser = this.userFactory.getNormalUser();
        testUser.setEnabled(true);
        testUser.setEmailVerified(true);

        return this.userService.save(testUser);
    }

    private OengusUser createAdminUser() {
        final var testUser = this.userFactory.getAdminUser();
        testUser.setEnabled(true);
        testUser.setEmailVerified(true);

        return this.userService.save(testUser);
    }
}
