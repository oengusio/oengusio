package app.oengus.configuration;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class RabbitMQConfiguration {

    private final String uri;
    private final String username;
    private final String password;

    public RabbitMQConfiguration(
        @Value("${rabbitmq.uri}") String uri,
        @Value("${rabbitmq.username}") String username,
        @Value("${rabbitmq.password}") String password
    ) {
        this.uri = uri;
        this.username = username;
        this.password = password;
    }

    @Bean
    public ConnectionFactory rabbitMqConnectionFactory() throws URISyntaxException, NoSuchAlgorithmException, KeyManagementException {
        final ConnectionFactory factory = new ConnectionFactory();

        factory.setUri(this.uri);
        factory.setUsername(this.username);
        factory.setPassword(this.password);

        return factory;
    }
}
