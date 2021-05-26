package app.oengus.entity.dto;

import java.time.ZonedDateTime;

public class MarathonBasicInfoDto {
    private String id;
    private String name;
    private ZonedDateTime startDate;
    private ZonedDateTime endDate;
    private ZonedDateTime submissionsEndDate;
    private boolean onsite;
    private boolean isPrivate;
    private String location;
    private String country;
    private String language;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ZonedDateTime getStartDate() {
        return this.startDate;
    }

    public void setStartDate(final ZonedDateTime startDate) {
        this.startDate = startDate;
    }

    public ZonedDateTime getEndDate() {
        return this.endDate;
    }

    public void setEndDate(final ZonedDateTime endDate) {
        this.endDate = endDate;
    }

    public ZonedDateTime getSubmissionsEndDate() {
        return submissionsEndDate;
    }

    public void setSubmissionsEndDate(final ZonedDateTime submissionsEndDate) {
        this.submissionsEndDate = submissionsEndDate;
    }

    public boolean getOnsite() {
        return this.onsite;
    }

    public void setOnsite(final boolean onsite) {
        this.onsite = onsite;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public boolean getPrivate() {
        return this.isPrivate;
    }

    public void setPrivate(final boolean aPrivate) {
        this.isPrivate = aPrivate;
    }
}
