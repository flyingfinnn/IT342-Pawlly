package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adoptions")
public class AdoptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adoptionID;

    private LocalDate adoptionDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String name;                // Name of the person applying for adoption
    private String address;             // Address of the applicant
    private String contactNumber;       // Contact number of the applicant
    private String petType;             // Type of pet being adopted
    private LocalDate submissionDate;   // Submission date for the adoption application

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    public AdoptionEntity() {
        this.adoptionDate = LocalDate.now();
        this.status = Status.PENDING; 
        this.submissionDate = LocalDate.now(); // Initialize submission date to current date
    }

    public Long getAdoptionID() {
        return adoptionID;
    }

    public void setAdoptionID(Long adoptionID) {
        this.adoptionID = adoptionID;
    }

    public LocalDate getAdoptionDate() {
        return adoptionDate;
    }

    public void setAdoptionDate(LocalDate adoptionDate) {
        this.adoptionDate = adoptionDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getPetType() {
        return petType;
    }

    public void setPetType(String petType) {
        this.petType = petType;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    // Methods for operations
    public void applyForAdoption() {
        this.status = Status.PENDING;
    }

    public void updateStatus(Status newStatus) {
        this.status = newStatus;
    }

    public String viewAdoptionStatus() {
        return "Adoption ID: " + adoptionID + ", Status: " + status + 
               ", Name: " + name + ", Type of Pet: " + petType;
    }
}
