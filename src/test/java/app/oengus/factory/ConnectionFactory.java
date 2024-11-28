package app.oengus.factory;

import app.oengus.domain.Connection;
import app.oengus.domain.SocialPlatform;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class ConnectionFactory extends AbstractFactory<Connection> {
    @NotNull
    @Override
    public Connection getObject() {
        final var con = new Connection();

        con.setId(faker.number().randomDigit());
        con.setPlatform(faker.options().option(SocialPlatform.values()));
        con.setUsername(faker.internet().username());

        return con;
    }

    @Override
    public Class<?> getObjectType() {
        return Connection.class;
    }
}
