package app.oengus.adapter.rest.helper;

import app.oengus.domain.submission.Submission;
import org.springframework.data.domain.Page;

import java.util.Set;

public class SubmissionHelpers {
    /**
     * Strips the answers and category codes from the submissions.
     * @param foundSubmissions the submissions to filter.
     */
    public static void stripSensitiveInfo(final Page<Submission> foundSubmissions) {
        foundSubmissions.forEach((submission) -> {
            // also strip the answers to prevent normal users from seeing them.
            submission.setAnswers(Set.of());
            submission.getGames().forEach((game) -> {
                game.getCategories().forEach((category) -> {
                    category.setCode(null);
                });
            });
        });
    }
}
