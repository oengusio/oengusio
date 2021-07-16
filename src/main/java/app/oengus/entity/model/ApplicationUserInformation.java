package app.oengus.entity.model;

import app.oengus.entity.constants.TShirtSize;
import app.oengus.spring.model.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "application_user_information")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ApplicationUserInformation {
    @Id
    @JsonBackReference
    @Column(name = "user_id")
    @JsonView(Views.Internal.class)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @Max(value = 255)
    @Column(name = "first_name")
    @JsonView(Views.Public.class)
    private String firstName;

    @NotNull
    @Max(value = 255)
    @Column(name = "last_name")
    @JsonView(Views.Public.class)
    private String lastName;

    @NotNull
    @Column(name = "birthdate")
    @JsonView(Views.Public.class)
    private LocalDate birthdate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tshrit_size")
    @JsonView(Views.Public.class)
    private TShirtSize tShirtSize;

    @Email
    @NotNull
    @Column(name = "email")
    @JsonView(Views.Public.class)
    private String email;

    @Nullable
    @Max(value = 20)
    @JsonView(Views.Public.class)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Nullable
    @Max(value = 255)
    @Column(name = "ice_1")
    @JsonView(Views.Public.class)
    private String ICE1;

    @Nullable
    @Max(value = 255)
    @Column(name = "ice_2")
    @JsonView(Views.Public.class)
    private String ICE2;

    @Nullable
    @Max(value = 255)
    @Column(name = "allergies")
    @JsonView(Views.Public.class)
    private String allergies;

    @Nullable
    @Max(value = 255)
    @Column(name = "diet")
    @JsonView(Views.Public.class)
    private String diet;

    public int getUserId() {
        return this.user.getId();
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public TShirtSize gettShirtSize() {
        return tShirtSize;
    }

    public void settShirtSize(TShirtSize tShirtSize) {
        this.tShirtSize = tShirtSize;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Nullable
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(@Nullable String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Nullable
    public String getICE1() {
        return ICE1;
    }

    public void setICE1(@Nullable String ICE1) {
        this.ICE1 = ICE1;
    }

    @Nullable
    public String getICE2() {
        return ICE2;
    }

    public void setICE2(@Nullable String ICE2) {
        this.ICE2 = ICE2;
    }

    @Nullable
    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(@Nullable String allergies) {
        this.allergies = allergies;
    }

    @Nullable
    public String getDiet() {
        return diet;
    }

    public void setDiet(@Nullable String diet) {
        this.diet = diet;
    }
}
