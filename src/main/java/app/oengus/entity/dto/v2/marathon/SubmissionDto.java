package app.oengus.entity.dto.v2.marathon;

import javax.annotation.Nullable;

public class SubmissionDto {
    private int submissionId;
    private int userId;
    private String username;
    @Nullable private String usernameJapanese;
    private int accepted;
    private long total;

    public int getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(int submissionId) {
        this.submissionId = submissionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Nullable
    public String getUsernameJapanese() {
        return usernameJapanese;
    }

    public void setUsernameJapanese(@Nullable String usernameJapanese) {
        this.usernameJapanese = usernameJapanese;
    }

    public int getAccepted() {
        return accepted;
    }

    public void setAccepted(int accepted) {
        this.accepted = accepted;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
