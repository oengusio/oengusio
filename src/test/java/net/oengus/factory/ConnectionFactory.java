package net.oengus.factory;

import app.oengus.domain.Connection;
import app.oengus.domain.SocialPlatform;

public class ConnectionFactory extends AbstractFactory<Connection> {
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
