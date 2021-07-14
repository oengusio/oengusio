package app.oengus.entity.model;

import app.oengus.entity.constants.SocialPlatform;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;

import java.io.Serializable;
import java.util.Objects;

@SuppressWarnings("unused")
@Entity
@Table(name = "social_accounts")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class SocialAccount implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "platform")
    @Enumerated(EnumType.STRING) /* Default is ORDINAL */
    private SocialPlatform platform;

    // TODO: platform based validation
    @NotNull
    @Size(max = 320)
    @Column(name = "username")
    private String username;

    @AssertTrue
    public boolean isBlaBlaTest() {
        System.out.println("USERNAME " + this.username);
        System.out.println("PLATFORM " + this.platform);
        System.out.println("USER " + this.user);


        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NotNull
    public User getUser() {
        return user;
    }

    public void setUser(@NotNull User user) {
        this.user = user;
    }

    @NotNull
    public SocialPlatform getPlatform() {
        return platform;
    }

    public void setPlatform(@NotNull SocialPlatform platform) {
        this.platform = platform;
    }

    @NotNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NotNull String username) {
        this.username = username;
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
