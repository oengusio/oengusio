package app.oengus.application;

import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.application.rabbitmq.IRabbitMQService;
import app.oengus.application.webhook.mapper.WebhookDtoMapper;
import app.oengus.domain.OengusBotUrl;
import app.oengus.domain.OengusUser;
import app.oengus.domain.marathon.Marathon;
import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Game;
import app.oengus.domain.submission.Submission;
import app.oengus.domain.webhook.WebhookSelectionDone;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;

// TODO: needs a lot of cleanup
@Service
@RequiredArgsConstructor
public class OengusWebhookService {
    private static final Logger LOG = LoggerFactory.getLogger(OengusWebhookService.class);

    private final OkHttpClient client = new OkHttpClient();

    private final ObjectMapper mapper;
    private final WebhookDtoMapper dtoMapper;
    private final IRabbitMQService rabbitMq;
    private final GamePersistencePort gamePersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;

    // TODO: replace bot webhook with settings.
    /// <editor-fold desc="event functions">
    // TODO: fix this when donations are fixed
    public void sendDonationEvent(final String url, final Object donation) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "DONATION")
            .putPOJO("donation", donation);

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    // TODO: no notification for joining multiplayer run
    public void sendNewSubmissionEvent(final String url, final Submission submission) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_ADD")
            .putPOJO("submission", this.dtoMapper.fromDomain(submission));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendSubmissionUpdateEvent(final String url, final Submission newSubmission, final Submission oldSubmission) throws IOException {
        final ObjectNode data = mapper.createObjectNode().put("event", "SUBMISSION_EDIT");
        data.putPOJO("submission", this.dtoMapper.fromDomain(newSubmission));
        data.putPOJO("original_submission", this.dtoMapper.fromDomain(oldSubmission));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendSubmissionDeleteEvent(final String url, final Submission submission, final OengusUser deletedBy) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_DELETE");
        data.putPOJO("submission", this.dtoMapper.fromDomain(submission));
        data.putPOJO("deleted_by", this.dtoMapper.fromDomain(deletedBy));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendGameDeleteEvent(final String url, final Game game, final OengusUser deletedBy) throws IOException {
        final var submission = this.submissionPersistencePort.getByGameId(game.getId());

        final ObjectNode data = mapper.createObjectNode()
            .put("event", "GAME_DELETE");
        data.putPOJO("game", game);
        data.putPOJO("deleted_by", this.dtoMapper.fromDomain(deletedBy));
        data.putPOJO("submission", this.dtoMapper.fromDomain(submission));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendCategoryDeleteEvent(final String url, final Category category, final OengusUser deletedBy) throws IOException {
        final var game = this.gamePersistencePort.findById(category.getGameId()).get();
        final var submission = this.submissionPersistencePort.findById(game.getSubmissionId()).get();

        // TODO: map these to DTOs
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "CATEGORY_DELETE");
        data.putPOJO("category", category);
        data.putPOJO("submission", this.dtoMapper.fromDomain(submission));
        data.putPOJO("game", game);
        data.putPOJO("deleted_by", this.dtoMapper.fromDomain(deletedBy));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendSelectionDoneEvent(final String url, final List<WebhookSelectionDone> selections) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "SELECTION_DONE");
        data.putPOJO("selections", selections);

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendSubmissionChangedStatus(final String url, final boolean submissionsOpen, final Marathon marathon) throws IOException {
        final var submissionData = mapper.createObjectNode()
            .put("open", submissionsOpen)
            .put("marathon_name", marathon.getName())
            .put("closes_at", mapper.writeValueAsString(marathon.getSubmissionsEndDate()));

        final ObjectNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_OPEN_STATUS_CHANGED")
            .set("submission_status", submissionData);

        if (handleOnBot(url)) {
            data.put("url", url);

            final String jsonData = mapper.writeValueAsString(data);

            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public boolean sendPingEvent(final String url) {
        try {
            final JsonNode data = mapper.createObjectNode().put("event", "PING");
            final RequestBody body = RequestBody.create(mapper.writeValueAsBytes(data), null);
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
    /// </editor-fold>

    private boolean handleOnBot(final String rawUrl) {
        if (!rawUrl.startsWith("oengus-bot")) {
            return false;
        }

        // TODO: no notification for joining multiplayer run
        // parse the url
        final OengusBotUrl url = new OengusBotUrl(rawUrl);

        // Checks for marathonId presence
        if (url.isEmpty()) {
            // still returning true since oengus-bot is no valid domain
            return true;
        }

        return url.has("editsub") || url.has("newsub") || url.has("donation");
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
                LOG.error("Failed to send hook event to " + url, e);
            }

            @Override
            public void onResponse(@Nonnull Call call, @Nonnull Response response) {
                response.close();
            }
        });
    }

    // TODO: implement in bot
    /*private void sendDonationEvent(final String marathon, final String channel, final Donation donation) {
        final DecimalFormat df = new DecimalFormat("#.##");
        String formattedAmount = df.format(donation.getAmount().doubleValue());

        final WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(
                donation.getNickname() + " donated to " + this.marathonService.getNameForCode(marathon),
                this.shortUrl + '/' + marathon + "/donations"
            ))
            .setDescription(String.format(
                "**Amount:** %s\n**Comment:** %s",
                formattedAmount,
                donation.getComment() == null ? "None" : donation.getComment()
            ));

        this.jda.sendMessage(channel, builder.build());
    }*/
}
