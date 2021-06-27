package app.oengus.service;

import app.oengus.entity.model.*;
import app.oengus.helper.OengusBotUrl;
import app.oengus.helper.TimeHelpers;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static app.oengus.helper.StringHelper.escapeMarkdown;
import static app.oengus.helper.WebhookHelper.createParameters;

@Service
public class OengusWebhookService {

    private static final Logger LOG = LoggerFactory.getLogger(OengusWebhookService.class);

    private final OkHttpClient client = new OkHttpClient();

    @Autowired
    private ObjectMapper mapper;

    @Value("${oengus.shortUrl}")
    private String shortUrl;

    @Autowired
    private DiscordApiService jda;

    @Autowired
    private MarathonService marathonService;

    // NOTE: this can only do two at the same time
    private final ScheduledExecutorService selectionTImer = Executors.newScheduledThreadPool(2, (r) -> {
        final Thread t = new Thread(r, "selection announcement thread");
        t.setDaemon(true);
        return t;
    });

    /// <editor-fold desc="event functions">
    public void sendDonationEvent(final String url, final Donation donation) throws IOException {
        if (handleOnBot(url, () -> createParameters("donation", donation))) {
            return;
        }

        final JsonNode data = mapper.createObjectNode()
            .put("event", "DONATION")
            .set("donation", parseJson(donation));

        callAsync(url, data);
    }

    public void sendNewSubmissionEvent(final String url, final Submission submission) throws IOException {
        if (handleOnBot(url, () -> createParameters("submission", submission))) {
            return;
        }

        final JsonNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_ADD")
            .set("submission", parseJson(submission));

        callAsync(url, data);
    }

    public void sendSubmissionUpdateEvent(final String url, final Submission newSubmission, final Submission oldSubmission) throws IOException {
        if (handleOnBot(url, () -> createParameters("submission", newSubmission, "oldSubmission", oldSubmission))) {
            return;
        }

        final ObjectNode data = mapper.createObjectNode().put("event", "SUBMISSION_EDIT");
        data.set("submission", parseJson(newSubmission));
        data.set("original_submission", parseJson(oldSubmission));

        callAsync(url, data);
    }

    public void sendSubmissionDeleteEvent(final String url, final Submission submission, final User deletedBy) throws IOException {
        if (handleOnBot(url, () -> createParameters("oldSubmission", submission, "deletedBy", deletedBy))) {
            return;
        }

        final ObjectNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_DELETE");
        data.set("submission", parseJson(submission));
        data.set("deleted_by", parseJson(deletedBy));

        callAsync(url, data);
    }

    public void sendGameDeleteEvent(final String url, final Game game, final User deletedBy) throws IOException {
        if (handleOnBot(url, () -> createParameters("delGame", game, "deletedBy", deletedBy))) {
            return;
        }

        final ObjectNode data = mapper.createObjectNode()
            .put("event", "GAME_DELETE");
        data.set("game", parseJson(game));
        data.set("deleted_by", parseJson(deletedBy));

        callAsync(url, data);
    }

    public void sendCategoryDeleteEvent(final String url, final Category category, final User deletedBy) throws IOException {
        if (handleOnBot(url, () -> createParameters("delCategory", category, "deletedBy", deletedBy))) {
            return;
        }

        final ObjectNode data = mapper.createObjectNode()
            .put("event", "CATEGORY_DELETE");
        data.set("category", parseJson(category));
        data.set("deleted_by", parseJson(deletedBy));

        callAsync(url, data);
    }

    public void sendSelectionDoneEvent(final String url, final List<Selection> selections) throws IOException {
        if (handleOnBot(url, () -> createParameters("selections", selections))) {
            return;
        }

        final ObjectNode data = mapper.createObjectNode()
            .put("event", "SELECTION_DONE");
        data.set("selections", parseJson(selections));

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

    @SuppressWarnings("unchecked")
    private boolean handleOnBot(final String rawUrl, final Supplier<Map<String, Object>> argsSupplier) {
        if (!rawUrl.startsWith("oengus-bot")) {
            return false;
        }

        // parse the url
        final OengusBotUrl url = new OengusBotUrl(rawUrl);

        if (url.isEmpty()) {
            // still returning true since oengus-bot is no valid domain
            return true;
        }

        // don't create the map if we don't need to
        final Map<String, Object> args = argsSupplier.get();
        final String marathon = url.get("marathon");

        if (url.has("donation") && args.containsKey("donation")) {
            sendDonationEvent(
                marathon,
                url.get("donation"),
                (Donation) args.get("donation")
            );

            return true;
        }

        if (url.has("editsub")) {
            final String editsub = url.get("editsub");

            if(args.containsKey("oldSubmission")) {
                final Submission oldSubmission = (Submission) args.get("oldSubmission");

                if (!args.containsKey("submission")) {
                    sendSubmissionDelete(
                        marathon,
                        editsub,
                        oldSubmission,
                        (User) args.get("deletedBy")
                    );
                    return true;
                }

                sendEditSubmission(
                    marathon,
                    editsub,
                    // get the new submission channel for when there's a new game added, or get the edit channel
                    url.has("newsub") ? url.get("newsub") : editsub,
                    (Submission) args.get("submission"),
                    oldSubmission
                );
            } else if (args.containsKey("delGame")) {
                sendGameDelete(
                    marathon,
                    editsub,
                    (Game) args.get("delGame"),
                    (User) args.get("deletedBy")
                );
            } else if (args.containsKey("delCategory")) {
                sendCategoryDelete(
                    marathon,
                    editsub,
                    (Category) args.get("delCategory"),
                    (User) args.get("deletedBy")
                );
            }
        }

        if (url.has("newsub")) {
            final String newsub = url.get("newsub");

            if (args.containsKey("submission") && !args.containsKey("oldSubmission")) {
                final String marathonName = this.marathonService.getNameForCode(marathon);

                sendNewSubmission(
                    marathon,
                    newsub,
                    (Submission) args.get("submission"),
                    marathonName
                );

                if (url.has("editsub")) {
                    sendNewSubmission(
                        marathon,
                        url.get("editsub"),
                        (Submission) args.get("submission"),
                        marathonName
                    );
                }
            } else if (args.containsKey("selections")) {
                // Detach the selections and filter on accepted ones
                final List<Selection> selections = ((List<Selection>) args.get("selections"))
                    .stream()
                    .filter((it) -> it.getStatus() == Status.VALIDATED)
                    .map(Selection::createDetached)
                    .collect(Collectors.toList());

                this.sendApprovedSelections(newsub, selections);
            }
        }

        return true;
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

    // TODO: extract to classes
    // TODO: does currently not detect a category that was removed
    private void sendEditSubmission(final String marathon, final String channel, final String newSubChannel,
                                    final Submission submission, final Submission oldSubmission) {
        final String marathonName = this.marathonService.getNameForCode(marathon);

        // if the submissions is the same, ignore it
        if (Objects.equals(submission, oldSubmission)) {
            return;
        }

        for (final Game newGame : submission.getGames()) {
            final Game oldGame = oldSubmission.getGames()
                .stream()
                .filter((g) -> g.getId() == newGame.getId())
                .findFirst()
                .orElse(null);

            // The game was just added
            if (oldGame == null) {
                sendNewGame(marathon, newSubChannel, newGame, marathonName);
                sendNewGame(marathon, channel, newGame, marathonName);
                continue;
            }

            // ignore the game if they are equal
            if (Objects.equals(newGame, oldGame)) {
                continue;
            }

            for (final Category newCategory : newGame.getCategories()) {
                final Category oldCategory = oldGame.getCategories()
                    .stream()
                    .filter((c) -> c.getId() == newCategory.getId())
                    .findFirst()
                    .orElse(null);

                if (oldCategory == null) {
                    sendNewCategory(marathon, newSubChannel, newCategory, marathonName);
                    sendNewCategory(marathon, channel, newCategory, marathonName);
                    continue;
                }

                // ignore the category if they are equal
                // also check for the game in case a description got changed
                if (Objects.equals(newCategory, oldCategory) && Objects.equals(newGame, oldGame)) {
                    continue;
                }

                sendUpdatedCategory(marathon, channel, newCategory, oldCategory, marathonName);
            }
        }
    }

    private void sendNewSubmission(final String marathon, final String channel, final Submission submission, final String marathonName) {
        for (final Game game : submission.getGames()) {
            sendNewGame(marathon, channel, game, marathonName);
        }
    }

    // send all categories
    private void sendNewGame(final String marathon, final String channel, final Game newGame, final String marathonName) {
        for (final Category category : newGame.getCategories()) {
            sendNewCategory(marathon, channel, category, marathonName);
        }
    }

    private void sendNewCategory(final String marathon, final String channel, final Category category, final String marathonName) {
        final Game game = category.getGame();
        final String username = game.getSubmission().getUser().getUsername();

        final WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(
                escapeMarkdown(username + " submitted a run to " + marathonName),
                this.shortUrl + '/' + marathon + "/submissions"
            ))
            .setDescription(String.format(
                "**Game:** %s\n**Category:** %s\n**Platform:** %s\n**Estimate:** %s",
                escapeMarkdown(game.getName()),
                escapeMarkdown(category.getName()),
                escapeMarkdown(game.getConsole()),
                TimeHelpers.formatDuration(category.getEstimate())
            ));

        this.jda.sendMessage(channel, builder.build());
    }

    private void sendUpdatedCategory(final String marathon, final String channel, final Category category, final Category oldCategory, final String marathonName) {
        final Game game = category.getGame();
        final Game oldGame = oldCategory.getGame();
        final String username = game.getSubmission().getUser().getUsername();

        final WebhookEmbedBuilder builder = new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(
                escapeMarkdown(username + " updated a run in " + marathonName),
                this.shortUrl + '/' + marathon + "/submissions"
            ));
        final StringBuilder sb = new StringBuilder();

        sb.append(String.format(
            "**Game:** %s\n**Category:** %s\n**Platform:** %s\n**Estimate:** %s",
            parseUpdatedString(game.getName(), oldGame.getName()),
            parseUpdatedString(category.getName(), oldCategory.getName()),
            parseUpdatedString(game.getConsole(), oldGame.getConsole()),
            parseUpdatedString(TimeHelpers.formatDuration(category.getEstimate()), TimeHelpers.formatDuration(oldCategory.getEstimate()))
        ));

        if (!category.getVideo().equals(oldCategory.getVideo())) {
            sb.append("\n**Video:** ").append(parseUpdatedString(category.getVideo(), oldCategory.getVideo()));
        }

        if (category.getType() != oldCategory.getType()) {
            sb.append("\n**Type:** ")
                .append(parseUpdatedString(category.getType().name(), oldCategory.getType().name()));
        }

        if (!category.getDescription().equals(oldCategory.getDescription())) {
            sb.append("\n**Category Description:** ")
                .append(parseUpdatedString(category.getDescription(), oldCategory.getDescription()));
        }

        if (!game.getDescription().equals(oldGame.getDescription())) {
            sb.append("\n**Game Description:** ")
                .append(parseUpdatedString(game.getDescription(), oldGame.getDescription()));
        }

        builder.setDescription(sb.toString());

        this.jda.sendMessage(channel, builder.build());
    }

    private String parseUpdatedString(String current, String old) {
        if (current.equals(old)) {
            return escapeMarkdown(current);
        }

        return escapeMarkdown(current + " (was " + old + ')');
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
                messages.add(categoryToEmbed(
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
            messages.add(categoryToEmbed(
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

        final WebhookEmbed webhookEmbed = categoryToEmbed(
            category,
            deletedBy,
            game.getSubmission().getUser(),
            marathonId,
            marathonName,
            game
        );

        this.jda.sendMessage(channel, webhookEmbed);
    }

    private WebhookEmbed categoryToEmbed(final Category category, final User deletedBy, final User owner,
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
