package app.oengus.service.rabbitmq;

public interface IRabbitMQService {

    void sendMessage(String message);
}
