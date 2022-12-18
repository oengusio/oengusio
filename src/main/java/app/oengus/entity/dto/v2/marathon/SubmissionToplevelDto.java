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
        private int id;
        private String username;
        @Nullable
        private String usernameJapanese;
        private int accepted;
        private int total;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
