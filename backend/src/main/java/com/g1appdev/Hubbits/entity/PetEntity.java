package com.g1appdev.Hubbits.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "pets")
public class PetEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int pid;

    private String name;
    private String type;
    private String breed;
    private int age;
    private String gender;
    private String description;
    private String photo;
    private String status;
    
    // New fields for user information
    private String userName;
    private String address;
    private String contactNumber;
    private String submissionDate;

    // Default constructor
    public PetEntity(){
        super();
    }
    
    // Updated constructor with new fields
    public PetEntity(int pid, String name, String type, String breed, int age, String gender, String description, String photo, String status, String userName, String address, String contactNumber, String submissionDate){
        super();
        this.pid = pid;
        this.name = name;
        this.type = type;
        this.age = age;
        this.breed = breed;
        this.gender = gender;
        this.description = description;
        this.photo = photo;
        this.status = status;
        this.userName = userName;
        this.address = address;
        this.contactNumber = contactNumber;
        this.submissionDate = submissionDate;
    }

    // Getter and setter methods for new fields
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    // Other getter and setter methods
    public int getPid(){
        return pid;
    } 

    public void setPid(int pid){
        this.pid = pid;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    public String getBreed(){
        return breed;
    }

    public void setBreed(String breed){
        this.breed = breed;
    }

    public int getAge(){
        return age;
    }

    public void setAge(int age){
        this.age = age;
    }

    public String getGender(){
        return gender;
    }

    public void setGender(String gender){
        this.gender = gender;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getPhoto(){
        return photo;
    }

    public void setPhoto(String photo){
        this.photo = photo;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }
}