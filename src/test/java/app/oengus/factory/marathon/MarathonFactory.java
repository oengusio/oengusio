package app.oengus.factory.marathon;

import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.factory.AbstractFactory;
import app.oengus.factory.OengusUserFactory;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class MarathonFactory extends AbstractFactory<Marathon> {
    private final OengusUserFactory oengusUserFactory;

    @NotNull
    @Override
    public Marathon getObject() {
        return this.withCreator(oengusUserFactory.getNormalUser());
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

        final var fakeStartDate = faker.date().future(20, TimeUnit.DAYS);

        marathon.setStartDate(fakeStartDate.toLocalDateTime().atZone(ZoneOffset.UTC));
        marathon.setEndDate(marathon.getStartDate().plusDays(
            faker.number().numberBetween(1, 69)
        ));

        // TODO: add settings as needed

        return marathon;
    }

    @Override
    public Class<?> getObjectType() {
        return Marathon.class;
    }
}