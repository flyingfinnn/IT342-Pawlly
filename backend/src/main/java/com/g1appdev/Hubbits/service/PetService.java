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
            pet.setStatus(newPetDetails.getStatus()); 
            return prepo.save(pet); 
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

}
