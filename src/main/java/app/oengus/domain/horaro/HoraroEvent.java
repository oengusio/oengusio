package app.oengus.domain.horaro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "name",
    "slug"
})
public class HoraroEvent {

    /**
     * (Required)
     */
    @JsonProperty("name")
    private String name;
    /**
     * (Required)
     */
    @JsonProperty("slug")
    private String slug;

    /**
     * (Required)
     */
    @JsonProperty("name")
    public String getName() {
        return this.name;
    }

    /**
     * (Required)
     */
    @JsonProperty("name")
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * (Required)
     */
    @JsonProperty("slug")
    public String getSlug() {
        return this.slug;
    }

    /**
     * (Required)
     */
    @JsonProperty("slug")
    public void setSlug(final String slug) {
        this.slug = slug;
    }

}
