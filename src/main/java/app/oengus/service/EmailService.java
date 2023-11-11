package app.oengus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmailService {
    private final JavaMailSender mailSender;
    private final String mailFrom;
    private final String mailFromName;

    public EmailService(
        JavaMailSender mailSender,
        @Value("${spring.mail.properties.mail.from.address}") String mailFrom,
        @Value("${spring.mail.properties.mail.from.name}") String mailFromName
    ) {
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.mailFromName = mailFromName;
    }

    public void sendTestEmail(String... to) {
        final SimpleMailMessage message = new SimpleMailMessage();

        System.out.println(Arrays.toString(to));

        message.setFrom(this.mailFromName + " <" + this.mailFrom + '>');
        message.setTo(to);
        message.setSubject("Oengus Test Email");
        message.setText("Hello world!");

        // TODO: Enable to send message
//        this.mailSender.send(message);
    }
}
