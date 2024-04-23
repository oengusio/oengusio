package app.oengus.domain.horaro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
// TODO: remove commented out properties?
@JsonPropertyOrder({
    "length",
    //"length_t",
    //"scheduled",
    //"scheduled_t",
    "data"
})
public class HoraroItem {

    @JsonProperty("length")
    private String length;
    /*@JsonProperty("length_t")
    private int lengthT;
    @JsonProperty("scheduled")
    private String scheduled;
    @JsonProperty("scheduled_t")
    private int scheduledT;*/
    /**
     * (Required)
     */
    @JsonProperty("data")
    private List<String> data = null;
    @JsonProperty("options")
    private String options = null;

    @JsonProperty("length")
    public String getLength() {
        return this.length;
    }

    @JsonProperty("length")
    public void setLength(final String length) {
        this.length = length;
    }

    /*@JsonProperty("length_t")
    public int getLengthT() {
        return this.lengthT;
    }

    @JsonProperty("length_t")
    public void setLengthT(final int lengthT) {
        this.lengthT = lengthT;
    }

    @JsonProperty("scheduled")
    public String getScheduled() {
        return this.scheduled;
    }

    @JsonProperty("scheduled")
    public void setScheduled(final String scheduled) {
        this.scheduled = scheduled;
    }

    @JsonProperty("scheduled_t")
    public int getScheduledT() {
        return this.scheduledT;
    }

    @JsonProperty("scheduled_t")
    public void setScheduledT(final int scheduledT) {
        this.scheduledT = scheduledT;
    }*/

    /**
     * (Required)
     */
    @JsonProperty("data")
    public List<String> getData() {
        return this.data;
    }

    /**
     * (Required)
     */
    @JsonProperty("data")
    public void setData(final List<String> data) {
        this.data = data;
    }

    @JsonProperty("options")
    public String getOptions() {
        return this.options;
    }

    @JsonProperty("options")
    public void setOptions(final String options) {
        this.options = options;
    }
}
