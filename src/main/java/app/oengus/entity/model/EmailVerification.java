package app.oengus.entity.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    public void setUser(User user) {
        this.user = user;
        this.userId = user.getId();
    }
}
