package app.oengus.service.rabbitmq;

import com.rabbitmq.client.*;
import io.sentry.Sentry;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
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

        // TODO: might make this durable
        this.channel.queueDeclareNoWait(QUEUE_NAME_BASE, false, false, false, Map.of());

//        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
//            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
//            System.out.println(" [x] Received '" + message + "'");
//        };
//        channel.basicConsume(QUEUE_NAME_BASE, true, deliverCallback, consumerTag -> { });
    }

    @Override
    public void queueBotMessage(String message) {
        try {
            this.channel.basicPublish(
                "",
                QUEUE_NAME_BASE,
                null,
//                new AMQP.BasicProperties.Builder()
//                    .contentType("application/json")
//                    .build(),
                message.getBytes(StandardCharsets.UTF_8)
            );
        } catch (Exception e) {
            e.printStackTrace();
            Sentry.captureException(e);
        }
    }

    @Override
    public void queueWebhookMessage(String url, String message) {
        // TODO
    }
}
