package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adoptions")
public class AdoptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "adoptionid") // Assuming DB column is adoption_id
    private Long adoptionID;

    @Column(name = "adoption_date") // Assuming DB column is adoption_date
    private LocalDate adoptionDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status") // Assuming DB column is status, if it were e.g. adoption_status, adjust here
    private Status status;

    @Column(name = "name") // Assuming DB column is name
    private String name;
    @Column(name = "address") // Assuming DB column is address
    private String address;
    @Column(name = "contact_number") // Explicitly mapping to snake_case
    private String contactNumber;
    @Column(name = "pet_type") // Explicitly mapping to snake_case
    private String petType;
    @Column(name = "breed") // Assuming DB column is breed
    private String breed;
    @Column(name = "description") // Assuming DB column is description
    private String description;
    @Column(name = "submission_date") // Assuming DB column is submission_date
    private LocalDate submissionDate;
    @Column(name = "photo") // Assuming DB column is photo
    private String photo;

    public enum Status {
        PENDING, APPROVED, REJECTED
    }

    public AdoptionEntity() {
        this.adoptionDate = LocalDate.now();
        this.status = Status.PENDING; 
        this.submissionDate = LocalDate.now(); 
    }

    // Getter and setter methods
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

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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
               ", Name: " + name + ", Type of Pet: " + petType +
               ", Breed: " + breed + ", Description: " + description;
    }
}
