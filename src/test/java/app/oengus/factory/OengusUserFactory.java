package app.oengus.factory;

import app.oengus.domain.OengusUser;
import app.oengus.domain.Role;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class OengusUserFactory extends AbstractFactory<OengusUser> {
    @NotNull
    @Override
    public OengusUser getObject() {
        final var user = new OengusUser(-1);

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
        user.setPassword("SuperSecurePassword");
        user.setNeedsPasswordReset(false);
        user.setCreatedAt(faker.timeAndDate().past(365, TimeUnit.DAYS).atZone(ZoneOffset.UTC));

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
