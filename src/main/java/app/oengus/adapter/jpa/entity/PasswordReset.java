package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "password_resets")
public class PasswordReset {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "token")
    private String token;

    @Column(name = "created_at")
    private LocalDate createdAt;

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }
}
