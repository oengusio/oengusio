package app.oengus.service;

import app.oengus.entity.model.Category;
import app.oengus.entity.model.Donation;
import app.oengus.entity.model.Game;
import app.oengus.entity.model.Submission;
import app.oengus.helper.TimeHelpers;
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
import java.text.DecimalFormat;
import java.util.Objects;

@Service
public class OengusWebhookService {

    private final OkHttpClient client = new OkHttpClient();
    @Autowired
    private ObjectMapper mapper;

    @Value("${oengus.baseUrl}")
    private String baseUrl;

    @Autowired
    private DiscordApiService jda;

    public void sendDonationEvent(final String url, final Donation donation) throws IOException {
        if (handleOnBot(url, null, null, donation)) {
            return;
        }

        final JsonNode data = mapper.createObjectNode()
            .put("event", "DONATION")
            .set("donation", mapper.valueToTree(donation));

        callAsync(url, data);
    }

    public void sendNewSubmissionEvent(final String url, final Submission submission) throws IOException {
        if (handleOnBot(url, submission, null, null)) {
            return;
        }

        final JsonNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_ADD")
            .set("submission", parseJson(submission));

        callAsync(url, data);
    }

    public void sendSubmissionUpdateEvent(final String url, final Submission newSubmission, final Submission oldSubmission) throws IOException {
        if (handleOnBot(url, newSubmission, oldSubmission, null)) {
            return;
        }

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
            // IllegalArgumentException in case of an invalid url
        } catch (IOException | IllegalArgumentException ignored) {
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
            // still returning true since oengus-bot is no valid domain
            return true;
        }

        final String marathon = url.get("marathon");

        if (oldSubmission != null && url.has("editsub")) {
            sendEditSubmission(
                marathon,
                url.get("editsub"),
                // get the new submission channel for when there's a new game added, or get the edit channel
                url.has("newsub") ? url.get("newsub") : url.get("editsub"),
                submission,
                oldSubmission
            );
        } else if (submission != null && url.has("newsub")) {
            sendNewSubmission(
                marathon,
                url.get("newsub"),
                submission
            );

            if (url.has("editsub")) {
                sendNewSubmission(
                    marathon,
                    url.get("editsub"),
                    submission
                );
            }
        } else if (url.has("donation")) {
            sendDonationEvent(
                marathon,
                url.get("donation"),
                donation
            );
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

    private void sendEditSubmission(final String marathon, final String channel, final String newSubChannel,
                                    final Submission submission, final Submission oldSubmission) {
        // if the submissions is the same, ignore it
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
                sendNewGame(marathon, newSubChannel, newGame);
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
                    sendNewCategory(marathon, newSubChannel, newCategory);
                    sendNewCategory(marathon, channel, newCategory);
                    continue;
                }

                // ignore the category if they are equal
                if (Objects.equals(newCategory, oldCategory)) {
                    continue;
                }

                sendUpdatedCategory(marathon, channel, newCategory, oldCategory);
            }
        }
    }

    private void sendNewSubmission(final String marathon, final String channel, final Submission submission) {
        for (final Game game : submission.getGames()) {
            sendNewGame(marathon, channel, game);
        }
    }

    // send all categories
    private void sendNewGame(final String marathon, final String channel, final Game newGame) {
        for (final Category category : newGame.getCategories()) {
            sendNewCategory(marathon, channel, category);
        }
    }

    private void sendNewCategory(final String marathon, final String channel, final Category category) {
        final Game game = category.getGame();
        final String username = game.getSubmission().getUser().getUsername();

        final EmbedBuilder builder = new EmbedBuilder()
            .setTitle(username + " submitted a run to " + marathon, this.baseUrl + "/marathon/" + marathon + "/submissions")
            .setDescription(String.format(
                "**Game:** %s\n**Category:** %s\n**Platform:** %s\n**Estimate:** %s",
                game.getName(),
                category.getName(),
                game.getConsole(),
                TimeHelpers.formatDuration(category.getEstimate())
            ));

        this.jda.sendMessage(channel, builder.build()).queue();
    }

    private void sendUpdatedCategory(final String marathon, final String channel, final Category category, final Category oldCategory) {
        final Game game = category.getGame();
        final Game oldGame = oldCategory.getGame();
        final String username = game.getSubmission().getUser().getUsername();

        final EmbedBuilder builder = new EmbedBuilder()
            .setTitle(username + " updated a run in " + marathon, this.baseUrl + "/marathon/" + marathon + "/submissions")
            .setDescription(String.format(
                "**Game:** %s\n**Category:** %s\n**Platform:** %s\n**Estimate:** %s",
                parseUpdatedString(game.getName(), oldGame.getName()),
                parseUpdatedString(category.getName(), oldCategory.getName()),
                parseUpdatedString(game.getConsole(), oldGame.getConsole()),
                parseUpdatedString(TimeHelpers.formatDuration(category.getEstimate()), TimeHelpers.formatDuration(oldCategory.getEstimate()))
            ));

        if (!category.getVideo().equals(oldCategory.getVideo())) {
            builder.appendDescription(
                "\n**Video:** " + parseUpdatedString(category.getVideo(), oldCategory.getVideo())
            );
        }

        this.jda.sendMessage(channel, builder.build()).queue();
    }

    private String parseUpdatedString(String current, String old) {
        if (current.equals(old)) {
            return current;
        }

        return current + " (was " + old + ')';
    }

    private void sendDonationEvent(final String marathon, final String channel, final Donation donation) {
        final DecimalFormat df = new DecimalFormat("#.##");
        String formattedAmount = df.format(donation.getAmount().doubleValue());

        final EmbedBuilder builder = new EmbedBuilder()
            .setTitle(donation.getNickname() + " donated to " + marathon, this.baseUrl + "/marathon/" + marathon + "/submissions")
            .setDescription(String.format(
                "**Amount:** %s\n**Comment:** %s",
                formattedAmount,
                donation.getComment() == null ? "None" : donation.getComment()
            ));

        this.jda.sendMessage(channel, builder.build()).queue();
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
            // marathon is required
            if (this.marathonId == null) {
                return true;
            }

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
