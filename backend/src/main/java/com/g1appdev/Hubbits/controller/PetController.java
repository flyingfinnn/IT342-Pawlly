package com.g1appdev.Hubbits.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import com.g1appdev.Hubbits.entity.PetEntity;
import com.g1appdev.Hubbits.service.PetService;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(method = RequestMethod.GET, path = "/api/pet")
public class PetController {
    @Autowired
    PetService pserv;

    @GetMapping("/test")
    public String print(){
        return "Hello, John Edward Selma";
    }

    @PostMapping("/postpetrecord")
public PetEntity postPetRecord(@RequestParam("name") String name,
                               @RequestParam("type") String type,
                               @RequestParam("breed") String breed,
                               @RequestParam("age") int age,
                               @RequestParam("gender") String gender,
                               @RequestParam("description") String description,
                               @RequestParam("photo") MultipartFile photo,
                               @RequestParam("status") String status,
                               @RequestParam("userName") String userName,
                               @RequestParam("address") String address,
                               @RequestParam("contactNumber") String contactNumber,
                               @RequestParam("submissionDate") String submissionDate) {
    try {
        if (photo == null || photo.isEmpty()) {
            throw new IllegalArgumentException("Photo is required for pet rehome records.");
        }

        // Save photo to a directory
        String fileName = StringUtils.cleanPath(photo.getOriginalFilename());
        Path uploadDir = Paths.get("uploads/pets");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        Path filePath = uploadDir.resolve(fileName);
        photo.transferTo(filePath);

        // Generate file URL
        String fileUrl = "http://localhost:8080/uploads/pets/" + fileName;

        // Create and save PetEntity
        PetEntity pet = new PetEntity(0, name, type, breed, age, gender, description, fileUrl, status, userName, address, contactNumber, submissionDate);
        return pserv.postPetRecord(pet);

    } catch (Exception e) {
        e.printStackTrace();
        return null; // Handle and log this error appropriately
    }
}


    @GetMapping("/getAllPets")
    public List<PetEntity> getAllPets(){
        return pserv.getAllPets();
    }

    @GetMapping("/getPet/{id}")
    public PetEntity getPetById(@PathVariable int id) {
        return pserv.getPetById(id);
    }

    @PutMapping("/putPetDetails")
    public PetEntity putPetDetails(@RequestParam int pid, @RequestBody PetEntity newPetDetails){
        return pserv.putPetDetails(pid, newPetDetails);
    }

    @DeleteMapping("/deletePetDetails/{pid}")
    public String deletePet(@PathVariable int pid){
        return pserv.deletePet(pid);
    }
}

