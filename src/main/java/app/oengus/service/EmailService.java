package app.oengus.service;

import app.oengus.entity.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class EmailService {
    // Really hate that these are not final
    @Value("${spring.mail.properties.mail.from.address}")
    private String mailFrom;
    @Value("${spring.mail.properties.mail.from.name}")
    private String mailFromName;

    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    /*public EmailService(
        JavaMailSender mailSender,
        @Value("${spring.mail.properties.mail.from.address}") String mailFrom,
        @Value("${spring.mail.properties.mail.from.name}") String mailFromName
    ) {
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.mailFromName = mailFromName;
    }*/

    private MimeMessage getDefaultEmailSettings() throws UnsupportedEncodingException, MessagingException {
        final MimeMessage message = this.mailSender.createMimeMessage();

        message.setFrom(new InternetAddress(this.mailFrom, this.mailFromName));

        return message;
    }

    public void sendTestEmail(String... to) {
        final SimpleMailMessage message = new SimpleMailMessage();

        System.out.println(Arrays.toString(to));

        message.setFrom(this.mailFromName + " <" + this.mailFrom + '>');
        message.setTo(to);
        message.setSubject("Oengus Test Email");
        message.setText("Hello world!");

        this.mailSender.send(message);
    }

    public void sendEmailVerification(User to, String verifyHash, String domain) throws Exception {
        final MimeMessage message = getDefaultEmailSettings();

        Context myContext = new Context();
        myContext.setVariable("domain", domain);
        myContext.setVariable("hash", verifyHash);

        String htmlTemplate = templateEngine.process("email-verification.html", myContext);

        message.setText(htmlTemplate, "UTF-8", "html");
        message.setSubject("Verify your email!");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.getMail(), to.getDisplayName()));

        this.mailSender.send(message);
    }
}
