package app.oengus.domain.webhook;

import app.oengus.domain.submission.Category;
import app.oengus.domain.submission.Selection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookSelectionDone extends Selection {
    private final Category category;
    private final int userId;

    public WebhookSelectionDone(int id, int categoryId, Category category, int userId) {
        super(id, categoryId);
        this.category = category;
        this.userId = userId;
    }
}
