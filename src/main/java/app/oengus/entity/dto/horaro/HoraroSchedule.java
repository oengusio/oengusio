package app.oengus.entity.dto.horaro;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
		"name",
		"slug",
		"timezone",
		"start",
		"start_t",
		"website",
		"twitter",
		"twitch",
		"theme",
		"secret",
		"updated",
		"url",
		"event",
		"columns",
		"items"
})
public class HoraroSchedule {

	@JsonProperty("name")
	private String name;
	@JsonProperty("slug")
	private String slug;
	@JsonProperty("timezone")
	private String timezone;
	@JsonProperty("start")
	private String start;
	@JsonProperty("start_t")
	private Integer startT;
	@JsonProperty("website")
	private String website;
	@JsonProperty("twitter")
	private String twitter;
	@JsonProperty("twitch")
	private String twitch;
	@JsonProperty("theme")
	private String theme;
	@JsonProperty("secret")
	private String secret;
	@JsonProperty("updated")
	private String updated;
	@JsonProperty("url")
	private String url;
	@JsonProperty("event")
	private HoraroEvent event;
	/**
	 * (Required)
	 */
	@JsonProperty("columns")
	private List<String> columns = null;
	/**
	 * (Required)
	 */
	@JsonProperty("items")
	private List<HoraroItem> items = null;

	@JsonProperty("name")
	public String getName() {
		return this.name;
	}

	@JsonProperty("name")
	public void setName(final String name) {
		this.name = name;
	}

	@JsonProperty("slug")
	public String getSlug() {
		return this.slug;
	}

	@JsonProperty("slug")
	public void setSlug(final String slug) {
		this.slug = slug;
	}

	@JsonProperty("timezone")
	public String getTimezone() {
		return this.timezone;
	}

	@JsonProperty("timezone")
	public void setTimezone(final String timezone) {
		this.timezone = timezone;
	}

	@JsonProperty("start")
	public String getStart() {
		return this.start;
	}

	@JsonProperty("start")
	public void setStart(final String start) {
		this.start = start;
	}

	@JsonProperty("start_t")
	public Integer getStartT() {
		return this.startT;
	}

	@JsonProperty("start_t")
	public void setStartT(final Integer startT) {
		this.startT = startT;
	}

	@JsonProperty("website")
	public String getWebsite() {
		return this.website;
	}

	@JsonProperty("website")
	public void setWebsite(final String website) {
		this.website = website;
	}

	@JsonProperty("twitter")
	public String getTwitter() {
		return this.twitter;
	}

	@JsonProperty("twitter")
	public void setTwitter(final String twitter) {
		this.twitter = twitter;
	}

	@JsonProperty("twitch")
	public String getTwitch() {
		return this.twitch;
	}

	@JsonProperty("twitch")
	public void setTwitch(final String twitch) {
		this.twitch = twitch;
	}

	@JsonProperty("theme")
	public String getTheme() {
		return this.theme;
	}

	@JsonProperty("theme")
	public void setTheme(final String theme) {
		this.theme = theme;
	}

	@JsonProperty("secret")
	public String getSecret() {
		return this.secret;
	}

	@JsonProperty("secret")
	public void setSecret(final String secret) {
		this.secret = secret;
	}

	@JsonProperty("updated")
	public String getUpdated() {
		return this.updated;
	}

	@JsonProperty("updated")
	public void setUpdated(final String updated) {
		this.updated = updated;
	}

	@JsonProperty("url")
	public String getUrl() {
		return this.url;
	}

	@JsonProperty("url")
	public void setUrl(final String url) {
		this.url = url;
	}

	@JsonProperty("event")
	public HoraroEvent getEvent() {
		return this.event;
	}

	@JsonProperty("event")
	public void setEvent(final HoraroEvent event) {
		this.event = event;
	}

	/**
	 * (Required)
	 */
	@JsonProperty("columns")
	public List<String> getColumns() {
		return this.columns;
	}

	/**
	 * (Required)
	 */
	@JsonProperty("columns")
	public void setColumns(final List<String> columns) {
		this.columns = columns;
	}

	/**
	 * (Required)
	 */
	@JsonProperty("items")
	public List<HoraroItem> getItems() {
		return this.items;
	}

	/**
	 * (Required)
	 */
	@JsonProperty("items")
	public void setItems(final List<HoraroItem> items) {
		this.items = items;
	}

}
