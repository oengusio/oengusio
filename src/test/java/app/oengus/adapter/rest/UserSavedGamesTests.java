package app.oengus.adapter.rest;

import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedCategoryCreateDto;
import app.oengus.adapter.rest.dto.v2.users.savedGames.SavedGameUpdateDto;
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

    @Test
    public void testUserCanUpdateSavedGame() throws IOException {
        final var supporterUser = this.createPatreonSupporterUserWithGames();
        final var currSavedGames = this.savedGameService.getByUser(supporterUser);
        final var selectedGame = currSavedGames.getFirst();

        final var updateBody = new SavedGameUpdateDto(
            "The Stanley Parable: Ultra Deluxe",
            selectedGame.getDescription(),
            "Steam Deck",
            selectedGame.getRatio(),
            !selectedGame.isEmulated()
        );
        final var jsonBody = this.objectMapper.writeValueAsString(updateBody);

        final var authToken = this.jwtPort.generateToken(supporterUser);

        final var bodyJson = assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/@me/saved-games/" + selectedGame.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        final var updatedGames = this.savedGameService.getByUser(supporterUser);
        final var updatedGame = updatedGames.getFirst();

        bodyJson.extractingPath("$.name")
            .asString()
            .isEqualTo(updatedGame.getName());

        bodyJson.extractingPath("$.description")
            .asString()
            .isEqualTo(updatedGame.getDescription());

        bodyJson.extractingPath("$.console")
            .asString()
            .isEqualTo(updatedGame.getConsole());

        bodyJson.extractingPath("$.ratio")
            .asString()
            .isEqualTo(updatedGame.getRatio());

        bodyJson.extractingPath("$.emulated")
            .asBoolean()
            .isEqualTo(updatedGame.isEmulated());

        // check that bool is correctly flipped
        assertThat(updatedGame.isEmulated()).isNotEqualTo(selectedGame.isEmulated());
    }

    @Test
    public void testUserCannotUpdateSavedCategoryFromOtherUser() throws IOException {
        final var randoWithGames = this.createUserWithGames();
        final var userThatWillUpdate = this.createPatreonSupporterUser();

        final var randoCategory = this.savedGameService.getByUser(randoWithGames)
            .getFirst()
            .getCategories()
            .getFirst();

        final var categoryPatch = new SavedCategoryCreateDto(
            randoCategory.getName(),
            randoCategory.getDescription(),
            randoCategory.getEstimate(),
            randoCategory.getVideo()
        );
        final var jsonBody = this.objectMapper.writeValueAsString(categoryPatch);

        final var authToken = this.jwtPort.generateToken(userThatWillUpdate);

        assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/@me/saved-games/%d/%d".formatted(randoCategory.getGameId(), randoCategory.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(401);
    }

    @Test
    public void testUserCanUpdateOwnSavedCategory() throws IOException {
        final var supporterUser = this.createPatreonSupporterUserWithGames();

        final var randoCategory = this.savedGameService.getByUser(supporterUser)
            .getFirst()
            .getCategories()
            .getFirst();

        final var categoryPatch = new SavedCategoryCreateDto(
            randoCategory.getName(),
            randoCategory.getDescription(),
            randoCategory.getEstimate(),
            "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );
        final var jsonBody = this.objectMapper.writeValueAsString(categoryPatch);

        final var authToken = this.jwtPort.generateToken(supporterUser);

        final var bodyJson = assertThat(
            this.mvc.patch()
                .header("Accept", "application/json")
                .uri("/v2/users/@me/saved-games/%d/%d".formatted(randoCategory.getGameId(), randoCategory.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody)
                .header("Authorization", "Bearer " + authToken)
        )
            .hasStatus(200)
            .bodyJson();

        final var updatedGames = this.savedGameService.getByUser(supporterUser);
        final var updatedCategory = updatedGames.getFirst().getCategories().getFirst();

        bodyJson.extractingPath("$.name")
            .asString()
            .isEqualTo(updatedCategory.getName());

        bodyJson.extractingPath("$.description")
            .asString()
            .isEqualTo(updatedCategory.getDescription());

        bodyJson.extractingPath("$.estimate")
            .asString()
            .isEqualTo(updatedCategory.getEstimate().toString());

        bodyJson.extractingPath("$.video")
            .asString()
            .isEqualTo("https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        bodyJson.extractingPath("$.video")
            .asString()
            .isEqualTo(updatedCategory.getVideo());
    }

    private void addGamesToUser(OengusUser user) {
        final var userId = user.getId();

        for (int i = 0; i < 2; i++) {
            final var newGame = this.gameFactory.withUserId(userId);

            for (int xxx = 0; xxx < 5; xxx++) {
                final var newCategory = this.categoryFactory.withGameId(-1);

                newGame.getCategories().add(newCategory);
            }

            this.savedGameService.save(newGame);
        }
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

    private OengusUser createPatreonSupporterUserWithGames() {
        final var user = this.createPatreonSupporterUser();

        this.addGamesToUser(user);

        return user;
    }

    private OengusUser createUserWithGames() {
        final var testUser = this.userFactory.getNormalUser();
        testUser.setEnabled(true);
        testUser.setEmailVerified(true);
        testUser.setSavedGamesPublic(true);

        final var savedUser = this.userService.save(testUser);

        this.addGamesToUser(savedUser);

        return savedUser;
    }
}
