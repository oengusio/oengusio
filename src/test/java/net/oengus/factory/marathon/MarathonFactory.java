package net.oengus.factory.marathon;

import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import lombok.RequiredArgsConstructor;
import net.oengus.factory.AbstractFactory;
import net.oengus.factory.OengusUserFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class MarathonFactory extends AbstractFactory<Marathon> {
//    private final OengusUserFactory oengusUserFactory;

    @NotNull
    @Override
    public Marathon getObject() {
        return null;// this.withCreator(oengusUserFactory.getNormalUser());
    }

    public Marathon withCreator(OengusUser creator) {
        final var marathon = new Marathon(
            faker.random().hex()
        );

        marathon.setCreator(creator);

        marathon.setName(faker.australia().animals());

        marathon.setDescription(faker.dumbAndDumber().quote());
        marathon.setOnsite(faker.bool().bool());
        marathon.setLocation(faker.address().city());
        marathon.setCountry(faker.address().countryCode());
        marathon.setLanguage(faker.nation().isoLanguage());
        marathon.setMaxGamesPerRunner(faker.number().numberBetween(1, 100));
        marathon.setMaxCategoriesPerGame(faker.number().numberBetween(1, 100));
        marathon.setHasMultiplayer(faker.bool().bool());
        marathon.setMaxNumberOfScreens(faker.number().numberBetween(1, 100));

        // TODO: add settings as needed

        return marathon;
    }

    @Override
    public Class<?> getObjectType() {
        return Marathon.class;
    }
}
