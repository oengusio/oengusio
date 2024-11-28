package app.oengus.application.rabbitmq;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile(value = {"sandbox", "test"})
public class DisabledRabbitMQService implements IRabbitMQService {
    public DisabledRabbitMQService() {
        System.out.println("===============================");
        System.out.println("RabbitMQ is disabled");
        System.out.println("===============================");
    }


    @Override
    public void queueBotMessage(String message) {
        // Do nothing
    }

    @Override
    public void queueWebhookMessage(String url, String message) {
        // TODO: Do I want this?
    }
}
