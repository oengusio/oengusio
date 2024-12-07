package app.oengus.domain;

import app.oengus.domain.exception.InvalidUsernameException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ConnectionTests {

    @Test
    public void testMastodonUsernameValidatesWithoutAtPrefix() {
        assertTrue(
            Connection.isUsernameValidForPlatform("oengusio@mas.to", SocialPlatform.MASTODON)
        );
    }

    @Test
    public void testMastodonUsernameValidatesWithAtPrefix() {
        assertTrue(
            Connection.isUsernameValidForPlatform("@oengusio@mas.to", SocialPlatform.MASTODON)
        );
    }

    @Test
    public void testSettingInvalidUsernameThrowsException() {
        final var connection = new Connection();
        connection.setPlatform(SocialPlatform.MASTODON);

        assertThrows(
            InvalidUsernameException.class,
            () -> connection.setUsername("not a valid username")
        );
    }
}
