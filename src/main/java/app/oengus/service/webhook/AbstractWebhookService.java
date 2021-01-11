package app.oengus.service.webhook;

import app.oengus.entity.model.Donation;
import app.oengus.entity.model.Submission;

import java.io.IOException;

public abstract class AbstractWebhookService {
    public abstract void sendDonationEvent(final String url, final Donation donation) throws IOException;

    public abstract void sendNewSubmissionEvent(final String url, final Submission submission) throws IOException;

    public abstract void sendSubmissionUpdateEvent(final String url, final Submission newSubmission, final Submission oldSubmission) throws IOException;

}
