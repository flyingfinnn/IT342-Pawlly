package com.g1appdev.Hubbits.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.g1appdev.Hubbits.entity.PetEntity;
import com.g1appdev.Hubbits.repository.PetRepository;

@Service
public class PetService {
    @Autowired
    PetRepository prepo;

    public PetService(){
        super();
    }

    // Add record 
    public PetEntity postPetRecord(PetEntity pet){
        return prepo.save(pet);
    }

    // Get all records
    public List<PetEntity> getAllPets(){
        return prepo.findAll();
    }

    public PetEntity getPetById(int id) {
        Optional<PetEntity> pet = prepo.findById(id);
        return pet.orElse(null);

    }
    // Update a record
    @SuppressWarnings("finally")
    public PetEntity putPetDetails(int pid, PetEntity newPetDetails){
        PetEntity pet = prepo.findById(pid).orElse(null);

        if (pet != null) {
            System.out.println("[DEBUG] Existing pet before update: " + pet);
            // Update all fields if provided (null check for each)
            if (newPetDetails.getName() != null) pet.setName(newPetDetails.getName());
            if (newPetDetails.getType() != null) pet.setType(newPetDetails.getType());
            if (newPetDetails.getBreed() != null) pet.setBreed(newPetDetails.getBreed());
            if (newPetDetails.getAge() != 0) pet.setAge(newPetDetails.getAge());
            if (newPetDetails.getGender() != null) pet.setGender(newPetDetails.getGender());
            if (newPetDetails.getDescription() != null) pet.setDescription(newPetDetails.getDescription());
            if (newPetDetails.getPhoto() != null) pet.setPhoto(newPetDetails.getPhoto());
            if (newPetDetails.getPhoto1() != null) pet.setPhoto1(newPetDetails.getPhoto1());
            if (newPetDetails.getPhoto2() != null) pet.setPhoto2(newPetDetails.getPhoto2());
            if (newPetDetails.getPhoto3() != null) pet.setPhoto3(newPetDetails.getPhoto3());
            if (newPetDetails.getPhoto4() != null) pet.setPhoto4(newPetDetails.getPhoto4());
            if (newPetDetails.getPhoto1Thumb() != null) pet.setPhoto1Thumb(newPetDetails.getPhoto1Thumb());
            if (newPetDetails.getStatus() != null) pet.setStatus(newPetDetails.getStatus());
            if (newPetDetails.getUserName() != null) pet.setUserName(newPetDetails.getUserName());
            if (newPetDetails.getAddress() != null) pet.setAddress(newPetDetails.getAddress());
            if (newPetDetails.getContactNumber() != null) pet.setContactNumber(newPetDetails.getContactNumber());
            if (newPetDetails.getWeight() != null) pet.setWeight(newPetDetails.getWeight());
            if (newPetDetails.getColor() != null) pet.setColor(newPetDetails.getColor());
            if (newPetDetails.getHeight() != null) pet.setHeight(newPetDetails.getHeight());
            if (newPetDetails.getSubmissionDate() != null) pet.setSubmissionDate(newPetDetails.getSubmissionDate());
            System.out.println("[DEBUG] Updated pet before save: " + pet);
            PetEntity saved = prepo.save(pet);
            System.out.println("[DEBUG] Saved pet: " + saved);
            return saved;
        } else {
            System.out.println("Pet " + pid + " not found.");
            return null;
        }
    }

    // Delete a record
    public String deletePet(int pid){
        String msg = "";
        if(prepo.findById(pid) != null){
            prepo.deleteById(pid);
            msg = "Pet Record successfully deleted!";
        }else{
            msg =  pid + " Not found!";
        }
        return msg;
    }

    public List<PetEntity> getPetsByUserName(String userName) {
        return prepo.findByUserName(userName);
    }

    public List<PetEntity> searchPetsByName(String name) {
        return prepo.findByNameContainingIgnoreCase(name);
    }

}