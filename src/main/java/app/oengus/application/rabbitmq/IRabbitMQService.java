package app.oengus.application.rabbitmq;

public interface IRabbitMQService {

    void queueBotMessage(String message);

    void queueWebhookMessage(String url, String message);
}
