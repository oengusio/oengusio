package app.oengus.service;

import app.oengus.entity.dto.v2.SelectionDto;
import app.oengus.entity.model.*;
import app.oengus.helper.OengusBotUrl;
import app.oengus.helper.TimeHelpers;
import app.oengus.service.rabbitmq.IRabbitMQService;
import app.oengus.spring.model.Views;
import club.minnced.discord.webhook.WebhookClient;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static app.oengus.helper.StringHelper.escapeMarkdown;

@Service
public class OengusWebhookService {
    private static final Logger LOG = LoggerFactory.getLogger(OengusWebhookService.class);

    private final OkHttpClient client = new OkHttpClient();

    private final String shortUrl;
    private final ObjectMapper mapper;
    private final DiscordApiService jda;
    private final MarathonService marathonService;
    private final IRabbitMQService rabbitMq;

    // NOTE: this can only do two at the same time
    private final ScheduledExecutorService selectionTImer = Executors.newScheduledThreadPool(2, (r) -> {
        final Thread t = new Thread(r, "selection announcement thread");
        t.setDaemon(true);
        return t;
    });

    public OengusWebhookService(
        @Value("${oengus.shortUrl}") String shortUrl,
        ObjectMapper mapper, DiscordApiService jda, MarathonService marathonService, IRabbitMQService rabbitMq
    ) {
        this.shortUrl = shortUrl;
        this.mapper = mapper;
        this.jda = jda;
        this.marathonService = marathonService;
        this.rabbitMq = rabbitMq;
    }

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

    public void sendSubmissionDeleteEvent(final String url, final Submission submission, final User deletedBy) throws IOException {
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

    public void sendGameDeleteEvent(final String url, final Game game, final User deletedBy) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "GAME_DELETE");
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

    public void sendCategoryDeleteEvent(final String url, final Category category, final User deletedBy) throws IOException {
        final ObjectNode data = mapper.createObjectNode()
            .put("event", "CATEGORY_DELETE");
        data.set("category", parseJson(category));
        data.set("deleted_by", parseJson(deletedBy));

        if (handleOnBot(url)) {
            data.put("url", url);
            final String jsonData = mapper.writeValueAsString(data);
            this.rabbitMq.queueBotMessage(jsonData);

            return;
        }

        callAsync(url, data);
    }

    public void sendSelectionDoneEvent(final String url, final List<Selection> selections) throws IOException {
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

    /// <editor-fold desc="sending functions" defaultstate="collapsed">
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

    private void sendDonationEvent(final String marathon, final String channel, final Donation donation) {
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
    }

    private void sendSubmissionDelete(final String marathonId, final String channel, final Submission submission, final User deletedBy) {
        final List<WebhookEmbed> messages = new ArrayList<>();
        final String marathonName = this.marathonService.getNameForCode(marathonId);

        for (final Game game : submission.getGames()) {
            for (final Category category : game.getCategories()) {
                messages.add(removedCategoryToEmbed(
                    category,
                    deletedBy,
                    submission.getUser(),
                    marathonId,
                    marathonName,
                    game
                ));
            }
        }

        try (WebhookClient client = this.jda.forChannel(channel)) {
            for (WebhookEmbed embed : messages) {
                client.send(embed);
            }
        }
    }

    private void sendGameDelete(final String marathonId, final String channel, final Game game, final User deletedBy) {
        final List<WebhookEmbed> messages = new ArrayList<>();
        final String marathonName = this.marathonService.getNameForCode(marathonId);

        for (final Category category : game.getCategories()) {
            messages.add(removedCategoryToEmbed(
                category,
                deletedBy,
                game.getSubmission().getUser(),
                marathonId,
                marathonName,
                game
            ));
        }

        try (WebhookClient client = this.jda.forChannel(channel)) {
            for (WebhookEmbed embed : messages) {
                client.send(embed);
            }
        }
    }

    private void sendCategoryDelete(final String marathonId, final String channel, final Category category, final User deletedBy) {
        final String marathonName = this.marathonService.getNameForCode(marathonId);

        final Game game = category.getGame();

        final WebhookEmbed webhookEmbed = removedCategoryToEmbed(
            category,
            deletedBy,
            game.getSubmission().getUser(),
            marathonId,
            marathonName,
            game
        );

        this.jda.sendMessage(channel, webhookEmbed);
    }

    private WebhookEmbed removedCategoryToEmbed(final Category category, final User deletedBy, final User owner,
                                                final String marathonId, final String marathonName, final Game game) {
        final String username = owner.getUsername();
        final String headerText = deletedBy.equals(owner) ?
            username + " deleted their own run" :
            deletedBy.getUsername() + " deleted a run by " + username;

        return new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(
                escapeMarkdown(headerText + " in " + marathonName),
                this.shortUrl + '/' + marathonId + "/submissions"
            ))
            .setDescription(String.format(
                "**Game:** %s\n**Category:** %s\n**Platform:** %s\n**Estimate:** %s",
                escapeMarkdown(game.getName()),
                escapeMarkdown(category.getName()),
                escapeMarkdown(game.getConsole()),
                TimeHelpers.formatDuration(category.getEstimate())
            ))
            .build();
    }

    private void sendApprovedSelections(final String channel, final List<Selection> selections) {
        final WebhookClient client = this.jda.forChannel(channel);
        final AtomicInteger i = new AtomicInteger(0);
        final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();

        client.send("Runs have been accepted, get ready for the announcements!");

        future.set(this.selectionTImer.scheduleAtFixedRate(() -> {
            try {
                if (i.get() >= selections.size()) {
                    client.send("Th-th-th-th-th-That's all, Folks.").thenRun(client::close);
                    future.get().cancel(true);
                    return;
                }

                final Selection selection = selections.get(i.get());

                this.sendSelectionApprovedEmbed(client, selection);
                i.incrementAndGet();
            } catch (Exception e) {
                LOG.error("Sending webhook failed", e);
            }
        }, 30L, 30L, TimeUnit.SECONDS));
    }

    private void sendSelectionApprovedEmbed(final WebhookClient client, final Selection selection) {
        final Category category = selection.getCategory();
        final Game game = category.getGame();
        final String submitter = game.getSubmission().getUser().getUsername();
        final List<String> runners = new ArrayList<>();

        runners.add(submitter);

        if (!category.getOpponents().isEmpty()) {
            for (final Opponent opponent : category.getOpponents()) {
                runners.add(opponent.getSubmission().getUser().getUsername());
            }
        }

        final WebhookEmbed embed = new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(
                "A run has been Accepted!",
                this.shortUrl + '/' + selection.getMarathon().getId()
            ))
            .setDescription(String.format(
                "**Submitted by:** %s\n**Game:** %s\n**Category:** %s\n**Estimate:** %s\n**Platform:** %s\n**Runners:** %s",
                escapeMarkdown(submitter),
                escapeMarkdown(game.getName()),
                escapeMarkdown(category.getName()),
                TimeHelpers.formatDuration(category.getEstimate()),
                escapeMarkdown(game.getConsole()),
                escapeMarkdown(String.join(", ", runners))
            ))
            .build();

        client.send(embed);
    }
    /// </editor-fold>
}
