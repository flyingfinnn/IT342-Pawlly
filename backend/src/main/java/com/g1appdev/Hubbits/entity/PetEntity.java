package com.g1appdev.Hubbits.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

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

    @Column(name = "photo1")
    private String photo1;

    @Column(name = "photo2")
    private String photo2;

    @Column(name = "photo3")
    private String photo3;

    @Column(name = "photo4")
    private String photo4;

    @Column(name = "photo1_thumb")
    private String photo1Thumb;

    @Column(name = "weight")
    private String weight;

    @Column(name = "color")
    private String color;

    @Column(name = "height")
    private String height;

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

    // Add new constructor for 4 photos
    public PetEntity(int pid, String name, String type, String breed, int age, String gender, String description, String photo, String photo1, String photo2, String photo3, String photo4, String status, String userName, String address, String contactNumber, String submissionDate){
        super();
        this.pid = pid;
        this.name = name;
        this.type = type;
        this.age = age;
        this.breed = breed;
        this.gender = gender;
        this.description = description;
        this.photo = photo;
        this.photo1 = photo1;
        this.photo2 = photo2;
        this.photo3 = photo3;
        this.photo4 = photo4;
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

    public String getPhoto(){ return photo; }
    public void setPhoto(String photo){ this.photo = photo; }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }

    public String getPhoto1() { return photo1; }
    public void setPhoto1(String photo1) { this.photo1 = photo1; }
    public String getPhoto2() { return photo2; }
    public void setPhoto2(String photo2) { this.photo2 = photo2; }
    public String getPhoto3() { return photo3; }
    public void setPhoto3(String photo3) { this.photo3 = photo3; }
    public String getPhoto4() { return photo4; }
    public void setPhoto4(String photo4) { this.photo4 = photo4; }

    public String getPhoto1Thumb() { return photo1Thumb; }
    public void setPhoto1Thumb(String photo1Thumb) { this.photo1Thumb = photo1Thumb; }

    public String getWeight() { return weight; }
    public void setWeight(String weight) { this.weight = weight; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getHeight() { return height; }
    public void setHeight(String height) { this.height = height; }
}