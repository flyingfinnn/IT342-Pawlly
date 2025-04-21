package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "volunteer_opportunities")
public class VolunteerOpportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int opportunityID;

    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    @Size(max = 5000, message = "Description cannot exceed 500 characters.")
    private String description;

    // Registration Period (start and end date for registration)
    @Column(nullable = false)
    private LocalDateTime registrationStartDate; // Start date and time for registration

    @Column(nullable = false)
    private LocalDateTime registrationEndDate; // End date and time for registration

    // Volunteer Work Date (actual event date)
    @Column(nullable = false)
    private LocalDateTime volunteerDatetime; // Date and time of the volunteer event

    private String location;
    
    // Fields for hours worked and volunteers needed
    private int hoursWorked;  // The number of hours a volunteer will work
    private int volunteersNeeded;  // The number of volunteers needed for the opportunity

    @OneToMany(mappedBy = "volunteerOpportunity", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<VolunteerSignUp> volunteerSignUps;

    // Field for volunteer image URL
    private String volunteerImageUrl;  // The URL of the volunteer image (instead of byte array)

    // New field for the creator (User)
    @Column(nullable = false)
    private int creatorId; // The ID of the user who created this opportunity

    // Getters and Setters

    public int getOpportunityID() {
        return opportunityID;
    }

    public void setOpportunityID(int opportunityID) {
        this.opportunityID = opportunityID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(LocalDateTime registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public LocalDateTime getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(LocalDateTime registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public LocalDateTime getVolunteerDatetime() {
        return volunteerDatetime;
    }

    public void setVolunteerDatetime(LocalDateTime volunteerDatetime) {
        this.volunteerDatetime = volunteerDatetime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public int getVolunteersNeeded() {
        return volunteersNeeded;
    }

    public void setVolunteersNeeded(int volunteersNeeded) {
        this.volunteersNeeded = volunteersNeeded;
    }

    public List<VolunteerSignUp> getVolunteerSignUps() {
        return volunteerSignUps;
    }

    public void setVolunteerSignUps(List<VolunteerSignUp> volunteerSignUps) {
        this.volunteerSignUps = volunteerSignUps;
    }

    public String getVolunteerImageUrl() {
        return volunteerImageUrl;
    }

    public void setVolunteerImageUrl(String volunteerImageUrl) {
        this.volunteerImageUrl = volunteerImageUrl;
    }

    // New getter and setter for creatorId
    public int getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(int creatorId) {
        this.creatorId = creatorId;
    }

    // Method to get the number of sign-ups
    public int getNumberOfSignUps() {
        return volunteerSignUps != null ? volunteerSignUps.size() : 0;
    }
}
