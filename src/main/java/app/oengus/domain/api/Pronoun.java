package app.oengus.domain.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Pronoun {
    private String canonicalName;
    private String[] aliases;

    public String getCanonicalName() {
        return canonicalName;
    }

    public void setCanonicalName(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String[] getAliases() {
        return aliases;
    }

    public void setAliases(String[] aliases) {
        this.aliases = aliases;
    }

    public String getEffectiveName() {
        if (this.aliases.length == 0) {
            return this.canonicalName;
        }

        return this.aliases[0];
    }
}
