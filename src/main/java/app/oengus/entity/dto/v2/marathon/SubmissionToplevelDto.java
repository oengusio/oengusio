package app.oengus.entity.dto.v2.marathon;

import javax.annotation.Nullable;
import java.util.List;

public class SubmissionToplevelDto {
    private List<UserData> data;

    public List<UserData> getData() {
        return data;
    }

    public void setData(List<UserData> data) {
        this.data = data;
    }

    public static class UserData {
        private int userId;
        private String username;
        @Nullable
        private String usernameJapanese;
        private int accepted;
        private long total;

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
}
