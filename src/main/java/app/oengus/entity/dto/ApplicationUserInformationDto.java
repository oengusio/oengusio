package app.oengus.entity.dto;

import app.oengus.domain.volunteering.TShirtSize;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.time.LocalDate;

public class ApplicationUserInformationDto {

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String firstName;

    @NotNull
    @NotBlank
    @Size(max = 255)
    private String lastName;

    @NotNull
    private LocalDate birthdate;

    @NotNull
    private TShirtSize tShirtSize;

    @Email
    @NotNull
    @NotBlank
    private String email;

    @Nullable
    @Size(max = 20)
    private String phoneNumber;

    @Nullable
    @Size(max = 255)
    private String ICE1;

    @Nullable
    @Size(max = 255)
    private String ICE2;

    @Nullable
    @Size(max = 255)
    private String allergies;

    @Nullable
    @Size(max = 255)
    private String diet;

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

    public void settShirtSize(String tShirtSize) {
        this.tShirtSize = TShirtSize.fromString(tShirtSize);
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
