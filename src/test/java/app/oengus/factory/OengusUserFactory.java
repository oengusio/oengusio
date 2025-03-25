package app.oengus.factory;

import app.oengus.domain.OengusUser;
import app.oengus.domain.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class OengusUserFactory extends AbstractFactory<OengusUser> {
    private final AtomicInteger idStore = new AtomicInteger();

    @NotNull
    @Override
    public OengusUser getObject() {
        final var user = new OengusUser(
            idStore.incrementAndGet()
        );

        user.setUsername(faker.internet().username().toLowerCase(Locale.ROOT).replace('.', '_'));
        user.setDisplayName(faker.name().firstName());
        user.setEmail(faker.internet().emailAddress());
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setEmailVerified(true);
        user.setCountry(faker.country().countryCode2());
        // good enough for now
        user.setLanguagesSpoken(List.of(
            faker.nation().isoLanguage(),
            faker.nation().isoLanguage()
        ));
        user.setNeedsPasswordReset(false);

        return user;
    }

    public OengusUser getNormalUser() {
        return this.getObject();
    }

    public OengusUser getAdminUser() {
        final var user = this.getObject();

        user.setRoles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));

        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return OengusUser.class;
    }
}
