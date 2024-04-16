package app.oengus.service;

import app.oengus.adapter.jpa.entity.SubmissionEntity;
import app.oengus.adapter.rest.dto.v2.SelectionDto;
import app.oengus.application.port.persistence.GamePersistencePort;
import app.oengus.application.port.persistence.SubmissionPersistencePort;
import app.oengus.domain.submission.Category;
import app.oengus.domain.OengusUser;
import app.oengus.domain.submission.Submission;
import app.oengus.entity.model.Donation;
import app.oengus.entity.model.GameEntity;
import app.oengus.entity.model.SelectionEntity;
import app.oengus.helper.OengusBotUrl;
import app.oengus.service.rabbitmq.IRabbitMQService;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
@RequiredArgsConstructor
public class OengusWebhookService {
    private static final Logger LOG = LoggerFactory.getLogger(OengusWebhookService.class);

    private final OkHttpClient client = new OkHttpClient();

    private final ObjectMapper mapper;
    private final IRabbitMQService rabbitMq;
    private final GamePersistencePort gamePersistencePort;
    private final SubmissionPersistencePort submissionPersistencePort;

    // NOTE: this can only do two at the same time
    private final ScheduledExecutorService selectionTImer = Executors.newScheduledThreadPool(2, (r) -> {
        final Thread t = new Thread(r, "selection announcement thread");
        t.setDaemon(true);
        return t;
    });

    // TODO: replace bot webhook with settings.
    /// <editor-fold desc="event functions">
    public void sendDonationEvent(final String url, final Donation donation) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "DONATION")
            .set("donation", parseJson(donation));

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
            .set("submission", parseJson(submission));

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
        data.set("submission", parseJson(newSubmission));
        data.set("original_submission", parseJson(oldSubmission));

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
        data.set("submission", parseJson(submission));
        data.set("deleted_by", parseJson(deletedBy));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendGameDeleteEvent(final String url, final GameEntity game, final OengusUser deletedBy) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "GAME_DELETE");
        data.set("game", parseJson(game));
        data.set("deleted_by", parseJson(deletedBy));
        data.set("submission", parseJson(game.getSubmission().fresh(false)));

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
        data.set("category", parseJson(category));
        data.set("submission", parseJson(submission));
        data.set("game", parseJson(game));
        data.set("deleted_by", parseJson(deletedBy));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendSelectionDoneEvent(final String url, final List<SelectionEntity> selections) throws IOException {
        final var dtos = selections.stream().map(SelectionDto::fromSelection).toList();

        final ObjectNode data = mapper.createObjectNode()
            .put("event", "SELECTION_DONE");
        data.set("selections", parseJson(dtos));

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
    /// </editor-fold>

    // TODO: use mappers for this.
    private JsonNode parseJson(final Object model) throws IOException {
        // hacky work around so we can use views
        final byte[] json = mapper.writerWithView(Views.Public.class).writeValueAsBytes(model);

        return mapper.readTree(json);
    }

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
