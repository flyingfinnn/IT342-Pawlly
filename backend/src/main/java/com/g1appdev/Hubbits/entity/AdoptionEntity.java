package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "adoptapplication")
public class AdoptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("user_id")
    @Column(nullable = false)
    private Long userId;

    @JsonProperty("pet_id")
    @Column(nullable = false)
    private Integer petId;

    @JsonProperty("household_type")
    private String householdType;

    @JsonProperty("household_ownership")
    private String householdOwnership;

    @JsonProperty("num_adults")
    private Integer numAdults;

    @JsonProperty("num_children")
    private Integer numChildren;

    @JsonProperty("other_pets")
    @Column(name = "other_pets")
    private Boolean otherPets;

    @JsonProperty("experience_with_pets")
    private String experienceWithPets;

    @JsonProperty("daily_routine")
    private String dailyRoutine;

    @JsonProperty("hours_alone_per_day")
    private Integer hoursAlonePerDay;

    @JsonProperty("reason_for_adoption")
    private String reasonForAdoption;

    @JsonProperty("pet_name")
    @Column(name = "pet_name")
    private String petName;

    @JsonProperty("accept_or_reject")
    @Column(name = "accept_or_reject")
    private String acceptOrReject;

    private String status = "pending";

    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;

    public AdoptionEntity() {
        this.status = "pending";
        this.createdAt = new java.sql.Timestamp(System.currentTimeMillis());
        this.updatedAt = new java.sql.Timestamp(System.currentTimeMillis());
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getPetId() { return petId; }
    public void setPetId(Integer petId) { this.petId = petId; }

    public String getHouseholdType() { return householdType; }
    public void setHouseholdType(String householdType) { this.householdType = householdType; }

    public String getHouseholdOwnership() { return householdOwnership; }
    public void setHouseholdOwnership(String householdOwnership) { this.householdOwnership = householdOwnership; }

    public Integer getNumAdults() { return numAdults; }
    public void setNumAdults(Integer numAdults) { this.numAdults = numAdults; }

    public Integer getNumChildren() { return numChildren; }
    public void setNumChildren(Integer numChildren) { this.numChildren = numChildren; }

    public Boolean getOtherPets() { return otherPets; }
    public void setOtherPets(Boolean otherPets) { this.otherPets = otherPets; }

    public String getExperienceWithPets() { return experienceWithPets; }
    public void setExperienceWithPets(String experienceWithPets) { this.experienceWithPets = experienceWithPets; }

    public String getDailyRoutine() { return dailyRoutine; }
    public void setDailyRoutine(String dailyRoutine) { this.dailyRoutine = dailyRoutine; }

    public Integer getHoursAlonePerDay() { return hoursAlonePerDay; }
    public void setHoursAlonePerDay(Integer hoursAlonePerDay) { this.hoursAlonePerDay = hoursAlonePerDay; }

    public String getReasonForAdoption() { return reasonForAdoption; }
    public void setReasonForAdoption(String reasonForAdoption) { this.reasonForAdoption = reasonForAdoption; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getAcceptOrReject() { return acceptOrReject; }
    public void setAcceptOrReject(String acceptOrReject) { this.acceptOrReject = acceptOrReject; }
}
