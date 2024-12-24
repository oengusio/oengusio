package app.oengus.adapter.jpa.entity;

import app.oengus.domain.Connection;
import app.oengus.domain.SocialPlatform;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Objects;

import static app.oengus.adapter.rest.dto.v1.UserDto.*;

@Getter
@Setter
@Entity
@Table(name = "social_accounts")
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "platform")
    @Enumerated(EnumType.STRING) /* Default is ORDINAL */
    private SocialPlatform platform;

    @NotNull
    @Size(max = 320)
    @Column(name = "username")
    private String username;

    // TODO: pull out to DTO
    @JsonIgnore
    @AssertTrue(message = "The username does not have a valid format for the platform")
    public boolean isUsernameValidForPlatform() {
        return Connection.isUsernameValidForPlatform(this.username, this.platform);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SocialAccount that = (SocialAccount) o;
        return platform == that.platform && username.equals(that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, username);
    }
}
