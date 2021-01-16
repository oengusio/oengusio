package app.oengus.service;

import app.oengus.entity.model.Category;
import app.oengus.entity.model.Donation;
import app.oengus.entity.model.Game;
import app.oengus.entity.model.Submission;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.dv8tion.jda.api.EmbedBuilder;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class OengusWebhookService {

    @Autowired
    private ObjectMapper mapper;

    @Value("${oengus.baseUrl}")
    private String baseUrl;

    private final OkHttpClient client = new OkHttpClient();

    public void sendDonationEvent(final String url, final Donation donation) throws IOException {
        final JsonNode data = mapper.createObjectNode()
            .put("event", "DONATION")
            .set("donation", mapper.valueToTree(donation));

        callAsync(url, data);
    }

    public void sendNewSubmissionEvent(final String url, final Submission submission) throws IOException {
        final JsonNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_ADD")
            .set("submission", parseJson(submission));

        callAsync(url, data);
    }

    public void sendSubmissionUpdateEvent(final String url, final Submission newSubmission, final Submission oldSubmission) throws IOException {
        final ObjectNode data = mapper.createObjectNode().put("event", "SUBMISSION_EDIT");
            data.set("submission", parseJson(newSubmission));
            data.set("original_submission", parseJson(oldSubmission));

        callAsync(url, data);
    }

    public boolean sendPingEvent(final String url) {
        try {
            final JsonNode data = mapper.createObjectNode().put("event", "PING");
            final RequestBody body = RequestBody.create(null, mapper.writeValueAsBytes(data));
            final Request request = new Request.Builder()
                .header("User-Agent", "oengus.io webhook")
                .header("Content-Type", "application/json")
                .url(url)
                .post(body)
                .build();

            try (final Response response = this.client.newCall(request).execute()) {
                return response.isSuccessful();
            }
        } catch (IOException ignored) {
            return false;
        }
    }

    private JsonNode parseJson(final Submission submission) throws IOException {
        // hacky work around so we can use views
        final String json = mapper.writerWithView(Views.Public.class).writeValueAsString(submission);

        return mapper.readTree(json);
    }

    private boolean handleOnBot(final String rawUrl, Submission submission, Submission oldSubmission, Donation donation) {
        if (!rawUrl.startsWith("oengus-bot")) {
            return false;
        }

        // parse the url
        final OengusBotUrl url = new OengusBotUrl(rawUrl);

        if (url.isEmpty()) {
            return false;
        }

        if (oldSubmission != null && url.has("editsub")) {
            //
        } else if (submission != null && url.has("newsub")) {
            //
            if (url.has("editsub")) {
                // also send it to the edit channel
            }
        } else if (url.has("donation")) {
            // donation is never null here
        }

        return true;
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
            public void onResponse(@Nonnull Call call, @Nonnull Response response) {
                response.close();
            }
        });
    }

    private void sendEditSubmission(final String marathon, final String channel, final Submission submission, final Submission oldSubmission) {
        // if the subssions is the same, ignore it
        if (Objects.equals(submission, oldSubmission)) {
            return;
        }

        for (final Game newGame : submission.getGames()) {
            final Game oldGame = oldSubmission.getGames()
                .stream()
                .filter((g) -> g.getId().equals(newGame.getId()))
                .findFirst()
                .orElse(null);

            // The game was just added
            if (oldGame == null) {
                sendNewGame(marathon, channel, newGame);
                continue;
            }

            // ignore the game if they are equal
            if (Objects.equals(newGame, oldGame)) {
                continue;
            }

            for (final Category newCategory : newGame.getCategories()) {
                final Category oldCategory = oldGame.getCategories()
                    .stream()
                    .filter((c) -> c.getId().equals(newCategory.getId()))
                    .findFirst()
                    .orElse(null);

                if (oldCategory == null) {
                    sendNewCategory(marathon, channel, newCategory);
                    continue;
                }
            }
        }
    }

    // send all categories
    private void sendNewGame(final String marathon, final String channel, final Game newGame) {
    }

    private void sendNewCategory(final String marathon, final String channel, final Category category) {
        //
    }

    private void sendUpdatedCategory(final String marathon, final String channel, final Category category) {
        final EmbedBuilder builder = new EmbedBuilder()
            .setTitle("A run has been updated", this.baseUrl + "/marathon/"+marathon+"/submissions")
            ;
    }

    private static class OengusBotUrl {
        private final String donation;
        private final String newSubmission;
        private final String editSubmission;
        private final String marathonId;

        OengusBotUrl(String url) {
            final MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(url)
                .build().getQueryParams();

            this.donation = queryParams.getFirst("donation");
            this.newSubmission = queryParams.getFirst("newsub");
            this.editSubmission = queryParams.getFirst("editsub");
            this.marathonId = queryParams.getFirst("marathon");
        }

        boolean isEmpty() {
            return this.donation == null && this.newSubmission == null && this.editSubmission == null;
        }

        boolean has(String type) {
            switch (type) {
                case "donation":
                    return this.donation != null;
                case "newsub":
                    return this.newSubmission != null;
                case "editsub":
                    return this.editSubmission != null;
                default:
                    return false;
            }
        }

        String get(String type) {
            switch (type) {
                case "donation":
                    return this.donation;
                case "newsub":
                    return this.newSubmission;
                case "editsub":
                    return this.editSubmission;
                case "marathon":
                    return this.marathonId;
                default:
                    return null;
            }
        }
    }
}
