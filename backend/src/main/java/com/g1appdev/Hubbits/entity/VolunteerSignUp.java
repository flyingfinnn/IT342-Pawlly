package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class VolunteerSignUp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int signUpID;

    private LocalDateTime dateRegistered;
    private String firstName;
    private String lastName;
    private String email;
    private String address;
    private String phoneNumber;
    private int hoursWorked; // Store individual hours worked for each signup

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "opportunity_id")
    @JsonBackReference
    private VolunteerOpportunity volunteerOpportunity;

    // Getters and Setters
    public int getSignUpID() {
        return signUpID;
    }

    public void setSignUpID(int signUpID) {
        this.signUpID = signUpID;
    }

    public LocalDateTime getDateRegistered() {
        return dateRegistered;
    }

    public void setDateRegistered(LocalDateTime dateRegistered) {
        this.dateRegistered = dateRegistered;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getHoursWorked() {
        return hoursWorked;
    }

    public void setHoursWorked(int hoursWorked) {
        this.hoursWorked = hoursWorked;
    }

    public VolunteerOpportunity getVolunteerOpportunity() {
        return volunteerOpportunity;
    }

    public void setVolunteerOpportunity(VolunteerOpportunity opportunity) {
        this.volunteerOpportunity = opportunity;
    }
    
    public int getOpportunityId() {
        if (volunteerOpportunity != null) {
            return volunteerOpportunity.getOpportunityID(); // Fetches opportunityId from the associated VolunteerOpportunity
        }
        return 0; // Return 0 or handle as necessary if volunteerOpportunity is null
    }
}