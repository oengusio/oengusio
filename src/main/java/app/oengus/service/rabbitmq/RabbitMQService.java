package app.oengus.service.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import io.sentry.Sentry;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

@Service
@Profile("!prod")
public class RabbitMQService implements IRabbitMQService {
    private static final String QUEUE_NAME_BASE = "oengus.bot";
    private final Connection connection;
    private final Channel channel;

    public RabbitMQService(ConnectionFactory rabbitMqConnectionFactory) throws IOException, TimeoutException {
        this.connection = rabbitMqConnectionFactory.newConnection();
        this.channel = this.connection.createChannel();
    }

    @Override
    public void sendMessage(String message) {
        try {
            this.channel.basicPublish(
                "",
                QUEUE_NAME_BASE,
                null,
                message.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.captureException(e);
        }
    }
}
