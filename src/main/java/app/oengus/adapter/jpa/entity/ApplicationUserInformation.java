package app.oengus.adapter.jpa.entity;

import app.oengus.domain.volunteering.TShirtSize;

import javax.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "application_user_information")
public class ApplicationUserInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @Size(max = 255)
    @Column(name = "first_name")
    private String firstName;

    @NotNull
    @Size(max = 255)
    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "birthdate")
    private LocalDate birthdate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tshirt_size")
    private TShirtSize tShirtSize;

    @Email
    @NotNull
    @Column(name = "email")
    private String email;

    @Nullable
    @Size(max = 20)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Nullable
    @Size(max = 255)
    @Column(name = "ice_1")
    private String ICE1;

    @Nullable
    @Size(max = 255)
    @Column(name = "ice_2")
    private String ICE2;

    @Nullable
    @Size(max = 255)
    @Column(name = "allergies")
    private String allergies;

    @Nullable
    @Size(max = 255)
    @Column(name = "diet")
    private String diet;

    @JoinColumn(name = "user_id")
    @OneToMany(fetch = FetchType.LAZY)
    private List<ApplicationEntry> applications;
}
