package app.oengus.adapter.rest;

import app.oengus.application.SavedGameService;
import app.oengus.application.UserService;
import app.oengus.application.port.persistence.PatreonStatusPersistencePort;
import app.oengus.application.port.security.JWTPort;
import app.oengus.domain.OengusUser;
import app.oengus.domain.PatreonPledgeStatus;
import app.oengus.domain.PledgeInfo;
import app.oengus.factory.OengusUserFactory;
import app.oengus.factory.user.SavedCategoryFactory;
import app.oengus.factory.user.SavedGameFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.io.IOException;

import static app.oengus.domain.Constants.MIN_PATREON_PLEDGE_AMOUNT;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = { @Autowired })
public class UserSavedGamesTests {
    private final ObjectMapper objectMapper;
    private final MockMvcTester mvc;

    private final UserService userService;
    private final SavedGameService savedGameService;
    private final JWTPort jwtPort;
    private final PatreonStatusPersistencePort patreonStatusPort;

    private final OengusUserFactory userFactory;
    private final SavedGameFactory gameFactory;
    private final SavedCategoryFactory categoryFactory;

    @Test
    public void userGamesCanBeRetrieved() {
        final var user = this.createUserWithGames();

        final var bodyJson = assertThat(
            this.mvc.get()
                .header("Accept", "application/json")
                .uri("/v2/users/" + user.getId() + "/saved-games")
        )
            .hasStatus(200)
            .hasHeader("Content-Type", "application/json")
            .bodyJson();

        bodyJson.extractingPath("$.data")
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .hasSize(2);

        bodyJson.extractingPath("$.data[0].categories")
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .hasSize(5);

        bodyJson.extractingPath("$.data[1].categories")
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .hasSize(5);
    }

    @Test
    public void userGameListIsEmptyWhenPreferenceIsHidden() {
        final var user = this.createUserWithGames();

        user.setSavedGamesPublic(false);

        this.userService.save(user);

        final var foundGames = this.savedGameService.getByUser(user);

        assertThat(foundGames).hasSize(2);

        final var bodyJson = assertThat(
            this.mvc.get()
                .header("Accept", "application/json")
                .uri("/v2/users/" + user.getId() + "/saved-games")
        )
            .hasStatus(200)
            .hasHeader("Content-Type", "application/json")
            .bodyJson();

        bodyJson.extractingPath("$.data")
            .asInstanceOf(InstanceOfAssertFactories.LIST)
            .hasSize(0);
    }

    @Test
    public void gettingOwnGamesFailsWithoutAuth() {
        assertThat(
            this.mvc.get()
                .uri("/v2/users/@me/saved-games")
                .header("Accept", "application/json")
        )
            .hasStatus(401);
    }

    // TODO: make tests for supporter status

    @Test
    public void testNonSupporterCannotAddGames() throws IOException {
        final var supporterUser = this.createUserWithGames();

        final var currSavedGames = this.savedGameService.getByUser(supporterUser);

        assertThat(currSavedGames).hasSize(2);

        final var authToken = this.jwtPort.generateToken(supporterUser);

        final var categoriesArray = this.objectMapper.createArrayNode();

        categoriesArray.add(
            this.objectMapper.createObjectNode()
                .put("name", "Give you up%")
                .put("description", "Sing the song I guess")
                .put("estimate", "PT2M35S")
                .put("video", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        );

        final var gameObj = this.objectMapper.createObjectNode()
            .put("name", "Never gonna give")
            .put("description", "Best song ever tbh")
            .put("console", "PC")
            .put("ratio", "16:9")
            .put("emulated", false)
            .set("categories", categoriesArray);

        final String patchBody = this.objectMapper.writeValueAsString(gameObj);

        assertThat(
            this.mvc.post()
                .header("Accept", "application/json")
                .uri("/v2/users/@me/saved-games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(401);

        final var newSavedGames = this.savedGameService.getByUser(supporterUser);

        assertThat(newSavedGames).hasSize(2);
    }

    @Test
    public void testUserCanCreateSavedGame() throws IOException {
        final var supporterUser = this.createPatreonSupporterUser();

        final var currSavedGames = this.savedGameService.getByUser(supporterUser);

        assertThat(currSavedGames).hasSize(0);

        final var authToken = this.jwtPort.generateToken(supporterUser);

        final var categoriesArray = this.objectMapper.createArrayNode();

        categoriesArray.add(
            this.objectMapper.createObjectNode()
                .put("name", "Give you up%")
                .put("description", "Sing the song I guess")
                .put("estimate", "PT2M35S")
                .put("video", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
        );

        final var gameObj = this.objectMapper.createObjectNode()
            .put("name", "Never gonna give")
            .put("description", "Best song ever tbh")
            .put("console", "PC")
            .put("ratio", "16:9")
            .put("emulated", false)
            .set("categories", categoriesArray);

        final String patchBody = this.objectMapper.writeValueAsString(gameObj);

        final var bodyJson = assertThat(
            this.mvc.post()
                .header("Accept", "application/json")
                .uri("/v2/users/@me/saved-games")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        final var newSavedGames = this.savedGameService.getByUser(supporterUser);

        assertThat(newSavedGames).hasSize(1);
    }

    private OengusUser createPatreonSupporterUser() {
        final var testUser = this.userFactory.getNormalUser();
        testUser.setEnabled(true);
        testUser.setEmailVerified(true);
        testUser.setSavedGamesPublic(true);
        testUser.setPatreonId("testId_"+System.currentTimeMillis());

        final var savedUser = this.userService.save(testUser);

        final var pledge = new PledgeInfo(savedUser.getPatreonId());

        pledge.setStatus(PatreonPledgeStatus.ACTIVE_PATRON);
        pledge.setPledgeAmount(MIN_PATREON_PLEDGE_AMOUNT);

        this.patreonStatusPort.save(pledge);

        return savedUser;
    }

    private OengusUser createUserWithGames() {
        final var testUser = this.userFactory.getNormalUser();
        testUser.setEnabled(true);
        testUser.setEmailVerified(true);
        testUser.setSavedGamesPublic(true);

        final var savedUser = this.userService.save(testUser);
        final var userId = savedUser.getId();

        for (int i = 0; i < 2; i++) {
            final var newGame = this.gameFactory.withUserId(userId);

            for (int xxx = 0; xxx < 5; xxx++) {
                final var newCategory = this.categoryFactory.withGameId(-1);

                newGame.getCategories().add(newCategory);
            }

            this.savedGameService.save(newGame);
        }

        return savedUser;
    }
}
