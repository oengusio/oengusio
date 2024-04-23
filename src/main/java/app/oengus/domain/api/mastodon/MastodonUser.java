package app.oengus.domain.api.mastodon;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This is called "Account" in their api
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MastodonUser {
    private String id;
    private String username;
    private String displayName;
    private String acct;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAcct() {
        return acct;
    }

    public void setAcct(String acct) {
        this.acct = acct;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
