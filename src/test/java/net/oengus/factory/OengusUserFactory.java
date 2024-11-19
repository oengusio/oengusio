package net.oengus.factory;

import app.oengus.domain.OengusUser;
import app.oengus.domain.Role;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class OengusUserFactory extends AbstractFactory<OengusUser> {
    @Override
    public OengusUser getObject() {
        final var user = new OengusUser(
            faker.number().randomDigit()
        );

        user.setUsername(faker.internet().username().toLowerCase(Locale.ROOT));
        user.setDisplayName(faker.name().firstName());
        user.setEmail(faker.internet().emailAddress());
        user.setEnabled(true);
        user.setRoles(Collections.singleton(Role.ROLE_USER));
        user.setEmailVerified(true);
        user.setCountry(faker.country().countryCode2());
        // good enough for now
        user.setLanguagesSpoken(List.of(
            faker.country().countryCode2(),
            faker.country().countryCode2()
        ));

        return user;
    }

    public OengusUser getNormalUser() {
        return this.getObject();
    }

    public OengusUser getAdminUser() {
        final var user = this.getObject();

        assert user != null;
        user.setRoles(Set.of(Role.ROLE_USER, Role.ROLE_ADMIN));

        return user;
    }

    @Override
    public Class<?> getObjectType() {
        return OengusUser.class;
    }
}
