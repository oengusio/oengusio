package app.oengus.helper;

import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class OengusBotUrl {
    private final String donation;
    private final String newSubmission;
    private final String editSubmission;
    private final String marathonId;

    public OengusBotUrl(String url) {
        final MultiValueMap<String, String> queryParams = UriComponentsBuilder.fromUriString(url)
            .build().getQueryParams();

        this.donation = queryParams.getFirst("donation");
        this.newSubmission = queryParams.getFirst("newsub");
        this.editSubmission = queryParams.getFirst("editsub");
        this.marathonId = queryParams.getFirst("marathon");
    }

    public boolean isEmpty() {
        // marathon is required
        if (this.marathonId == null) {
            return true;
        }

        return this.donation == null && this.newSubmission == null && this.editSubmission == null;
    }

    public boolean has(String type) {
        return switch (type) {
            case "donation" -> this.donation != null;
            case "newsub" -> this.newSubmission != null;
            case "editsub" -> this.editSubmission != null;
            default -> false;
        };
    }

    public String get(String type) {
        return switch (type) {
            case "donation" -> this.donation;
            case "newsub" -> this.newSubmission;
            case "editsub" -> this.editSubmission;
            case "marathon" -> this.marathonId;
            default -> null;
        };
    }
}
