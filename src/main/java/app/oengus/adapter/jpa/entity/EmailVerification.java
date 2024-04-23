package app.oengus.adapter.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "email_verification")
public class EmailVerification {
    @Id
    @Column(name = "user_id")
    private Integer userId;

    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @Column(name = "verify_hash")
    private String verificationHash;

    // TODO: Schedule automatic removal after 3 hours.
    @Column(name = "created_at")
    private LocalDate createdAt;

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }
}
