package app.oengus.adapter.jpa.entity;

import app.oengus.domain.volunteering.TShirtSize;
import app.oengus.adapter.rest.Views;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import javax.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "application_user_information")
public class ApplicationUserInformation {
    @Id
    @JsonView(Views.Public.class)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonBackReference
    @JoinColumn(name = "user_id")
    @JsonView(Views.Internal.class)
    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    @NotNull
    @Size(max = 255)
    @Column(name = "first_name")
    @JsonView(Views.Public.class)
    private String firstName;

    @NotNull
    @Size(max = 255)
    @Column(name = "last_name")
    @JsonView(Views.Public.class)
    private String lastName;

    @NotNull
    @Column(name = "birthdate")
    @JsonView(Views.Public.class)
    private LocalDate birthdate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tshirt_size")
    @JsonView(Views.Public.class)
    private TShirtSize tShirtSize;

    @Email
    @NotNull
    @Column(name = "email")
    @JsonView(Views.Public.class)
    private String email;

    @Nullable
    @Size(max = 20)
    @JsonView(Views.Public.class)
    @Column(name = "phone_number")
    private String phoneNumber;

    @Nullable
    @JsonIgnore
    @Size(max = 255)
    @Column(name = "ice_1")
    @JsonView(Views.Public.class)
    private String ICE1;

    @Nullable
    @JsonIgnore
    @Size(max = 255)
    @Column(name = "ice_2")
    @JsonView(Views.Public.class)
    private String ICE2;

    @Nullable
    @Size(max = 255)
    @Column(name = "allergies")
    @JsonView(Views.Public.class)
    private String allergies;

    @Nullable
    @Size(max = 255)
    @Column(name = "diet")
    @JsonView(Views.Public.class)
    private String diet;

    @JoinColumn(name = "user_id")
    @JsonView(Views.Internal.class)
    @OneToMany(fetch = FetchType.LAZY)
    private List<ApplicationEntry> applications;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

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

    @JsonIgnore
    public TShirtSize gettShirtSize() {
        return tShirtSize;
    }

    @JsonGetter("tShirtSize")
    public String gettShirtSizeDisplay() {
        return tShirtSize.getDisplay();
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
    @JsonGetter("ice1")
    public String getICE1() {
        return ICE1;
    }

    public void setICE1(@Nullable String ICE1) {
        this.ICE1 = ICE1;
    }


    @Nullable
    @JsonGetter("ice2")
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

    public List<ApplicationEntry> getApplications() {
        return applications;
    }
}
