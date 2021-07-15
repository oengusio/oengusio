package app.oengus.entity.dto;

import app.oengus.entity.model.SocialAccount;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import javax.xml.bind.DatatypeConverter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class UserProfileDto {

    private int id;
    private String username;
    private String usernameJapanese;
    private boolean enabled;
    private List<SocialAccount> connections;
    private List<UserHistoryDto> history;
    private List<MarathonBasicInfoDto> moderatedMarathons;
    @Nullable
    private String pronouns;
    private final String avatarHash;
    private boolean banned;

    public UserProfileDto(String email) {
        this.history = new ArrayList<>();
        this.moderatedMarathons = new ArrayList<>();

        String tmpHash = "00000000000000000000000000000000";

        try {
            final String emailLower = email.toLowerCase();
            final byte[] md5s = MessageDigest.getInstance("MD5").digest(emailLower.getBytes());
            tmpHash = DatatypeConverter.printHexBinary(md5s).toLowerCase();
            // should never happen
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        this.avatarHash = tmpHash;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    public String getUsernameJapanese() {
        return this.usernameJapanese;
    }

    public void setUsernameJapanese(final String usernameJapanese) {
        this.usernameJapanese = usernameJapanese;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public List<UserHistoryDto> getHistory() {
        return this.history;
    }

    public void setHistory(final List<UserHistoryDto> history) {
        this.history = history;
    }

    public List<MarathonBasicInfoDto> getModeratedMarathons() {
        return this.moderatedMarathons;
    }

    public void setModeratedMarathons(final List<MarathonBasicInfoDto> moderatedMarathons) {
        this.moderatedMarathons = moderatedMarathons;
    }

    public List<SocialAccount> getConnections() {
        return connections;
    }

    public void setConnections(List<SocialAccount> connections) {
        this.connections = connections;
    }

    @NotNull
    public String[] getPronouns() {
        if (pronouns == null) {
            return new String[]{};
        }

        return pronouns.split(",");
    }

    public void setPronouns(@Nullable String pronouns) {
        this.pronouns = pronouns;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public String getAvatarHash() {
        return avatarHash;
    }
}
