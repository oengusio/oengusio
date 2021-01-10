package app.oengus.service;

import app.oengus.entity.model.Donation;
import app.oengus.entity.model.Submission;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;

@Service
public class OengusWebhookService {

    @Autowired
    private ObjectMapper mapper;

    private final OkHttpClient client = new OkHttpClient();

    public void sendDonationEvent(final String url, final Donation donation) throws IOException {
        final JsonNode data = mapper.createObjectNode()
            .put("event", "DONATION")
            .set("donation", mapper.valueToTree(donation));

        String s = mapper.writeValueAsString(data);
        System.out.println(s);

        callAsync(url, data);
    }

    public void sendNewSubmissionEvent(final String url, final Submission submission) throws IOException {
        final JsonNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_ADD")
            .set("submission", mapper.valueToTree(submission));

        callAsync(url, data);
    }

    public void sendSubmissionUpdateEvent(final String url, final Submission newSubmission, final Submission oldSubmission) throws IOException {
        final ObjectNode data = mapper.createObjectNode().put("event", "SUBMISSION_EDIT");
            data.set("submission", mapper.valueToTree(newSubmission));
            data.set("original_submission", mapper.valueToTree(oldSubmission));

        callAsync(url, data);
    }

    public boolean isWebhookOnline(String url) {
        try {
            final JsonNode data = mapper.createObjectNode().put("event", "PING");
            final RequestBody body = RequestBody.create(null, mapper.writeValueAsBytes(data));
            final Request request = new Request.Builder()
                .header("User-Agent", "oengus.io webhook")
                .header("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build();

            return this.client.newCall(request)
                .execute()
                // status code was 2xx
                .isSuccessful();
        } catch (IOException e) {
            return false;
        }
    }

    private void callAsync(final String url, final JsonNode data) throws IOException {
        final RequestBody body = RequestBody.create(null, mapper.writeValueAsBytes(data));
        final Request request = new Request.Builder()
            .header("User-Agent", "oengus.io webhook")
            .header("Content-Type", "application/json")
            .url(url)
            .post(body)
            .build();

        this.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
                //
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
                response.close();
            }
        });
    }
}
