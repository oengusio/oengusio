package app.oengus.service.webhook;

import app.oengus.entity.model.*;
import app.oengus.helper.BeanHelper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Profile("prod")
public class OengusWebhookService extends AbstractWebhookService {

    @Autowired
    private ObjectMapper mapper;

    private final OkHttpClient client = new OkHttpClient();

    @Override
    public void sendDonationEvent(final String url, final Donation donation) throws IOException {
        final JsonNode data = mapper.createObjectNode()
            .put("event", "DONATION")
            .set("donation", mapper.valueToTree(donation));

        String s = mapper.writeValueAsString(data);
        System.out.println(s);

        callAsync(url, data);
    }

    @Override
    public void sendNewSubmissionEvent(final String url, final Submission submission) throws IOException {
        final JsonNode data = mapper.createObjectNode()
            .put("event", "SUBMISSION_ADD")
            .set("submission", mapper.valueToTree(fixSubmission(submission)));

        callAsync(url, data);
    }

    @Override
    public void sendSubmissionUpdateEvent(final String url, final Submission newSubmission, final Submission oldSubmission) throws IOException {
        final ObjectNode data = mapper.createObjectNode().put("event", "SUBMISSION_EDIT");
            data.set("submission", mapper.valueToTree(fixSubmission(newSubmission)));
            data.set("original_submission", mapper.valueToTree(fixSubmission(oldSubmission)));

        callAsync(url, data);
    }

    /**
     * Clones the submission to remove circular references
     *
     * @param submission The submission to clone
     * @return The fixed submission
     */
    private Submission fixSubmission(final Submission submission) {
        final Submission hookSubmission = new Submission();

        // Ignore properties that we are copying manually
        BeanHelper.copyProperties(submission, hookSubmission, "games", "answers", "opponents");

        final Set<Game> games = submission.getGames().stream().map((game) -> {
            final Game g = new Game();
            BeanHelper.copyProperties(game, g, "submission");

            final List<Category> categories = game.getCategories().stream().map((category) -> {
                final Category c = new Category();
                BeanHelper.copyProperties(category, c, "selection");

                return c;
            }).collect(Collectors.toList());

            g.setCategories(categories);

            return g;
        }).collect(Collectors.toSet());

        hookSubmission.setGames(games);

        final Supplier<SortedSet<Answer>> supplier = TreeSet::new;
        final SortedSet<Answer> answers = submission.getAnswers().stream().map((answer) -> {
            final Answer a = new Answer();
            BeanHelper.copyProperties(answer, a, "submission");
            return a;
        }).collect(Collectors.toCollection(supplier));

        hookSubmission.setAnswers(answers);

        final Set<Opponent> opponents = submission.getOpponents().stream().map((opponent) -> {
            final Opponent o = new Opponent();
            BeanHelper.copyProperties(opponent, o, "submission");
            return o;
        }).collect(Collectors.toSet());

        hookSubmission.setOpponents(opponents);

        return hookSubmission;
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
}
