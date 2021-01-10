package app.oengus.service;

import app.oengus.entity.model.Donation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        final RequestBody body = RequestBody.create(mapper.writeValueAsBytes(data));
        final Request request = new Request.Builder()
            .header("User-Agent", "oengus.io webhook")
            .header("Content-Type", "application/json")
            .url(url)
            .post(body)
            .build();

        this.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                //
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                response.close();
            }
        });
    }

    public boolean isWebhookOnline(String url) {
        try {
            final JsonNode data = mapper.createObjectNode().put("event", "PING");
            final RequestBody body = RequestBody.create(mapper.writeValueAsBytes(data));
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
}
