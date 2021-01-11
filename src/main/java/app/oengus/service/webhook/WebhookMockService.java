package app.oengus.service.webhook;

import app.oengus.entity.model.Donation;
import app.oengus.entity.model.Submission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!prod")
public class WebhookMockService extends AbstractWebhookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookMockService.class);

    @Override
    public void sendDonationEvent(String url, Donation donation) {
        LOGGER.info("[MOCK] Sending donation event to {}", url);
    }

    @Override
    public void sendNewSubmissionEvent(String url, Submission submission) {
        LOGGER.info("[MOCK] Sending new submission event to {}", url);
    }

    @Override
    public void sendSubmissionUpdateEvent(String url, Submission newSubmission, Submission oldSubmission) {
        LOGGER.info("[MOCK] Sending submission update event to {}", url);
    }
}
