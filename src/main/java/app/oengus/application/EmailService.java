package app.oengus.application;

import app.oengus.domain.exception.WrappedException;
import app.oengus.domain.OengusUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {
    private final String baseUrl;
    private final String mailFrom;
    private final String mailFromName;
    private final SpringTemplateEngine templateEngine;
    private final JavaMailSender mailSender;

    public EmailService(
        JavaMailSender mailSender,
        @Value("${spring.mail.properties.mail.from.address}") String mailFrom,
        @Value("${spring.mail.properties.mail.from.name}") String mailFromName,
        @Value("${oengus.baseUrl}") String baseUrl,
        SpringTemplateEngine templateEngine) {
        this.baseUrl = baseUrl;
        this.mailSender = mailSender;
        this.mailFrom = mailFrom;
        this.mailFromName = mailFromName;
        this.templateEngine = templateEngine;
    }

    private MimeMessage getDefaultEmailSettings() throws UnsupportedEncodingException, MessagingException {
        final MimeMessage message = this.mailSender.createMimeMessage();

        message.setFrom(new InternetAddress(this.mailFrom, this.mailFromName));

        return message;
    }

    public void sendPasswordReset(OengusUser to, String resetToken) {
        try {
            final MimeMessage message = getDefaultEmailSettings();

            Context myContext = new Context();
            myContext.setVariable("domain", this.baseUrl);
            myContext.setVariable("token", resetToken);

            String htmlTemplate = templateEngine.process("password-reset.html", myContext);

            message.setText(htmlTemplate, "UTF-8", "html");
            message.setSubject("Reset your password.");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.getEmail(), to.getDisplayName()));

            this.mailSender.send(message);
        } catch (Exception e) {
            throw new WrappedException(e);
        }
    }

    public void sendEmailVerification(OengusUser to, String verifyHash) {
        try {
            final MimeMessage message = getDefaultEmailSettings();

            Context myContext = new Context();
            myContext.setVariable("domain", this.baseUrl);
            myContext.setVariable("hash", verifyHash);

            String htmlTemplate = templateEngine.process("email-verification.html", myContext);

            message.setText(htmlTemplate, "UTF-8", "html");
            message.setSubject("Verify your email!");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to.getEmail(), to.getDisplayName()));

            this.mailSender.send(message);
        } catch (Exception e) {
            throw new WrappedException(e);
        }
    }
}
