package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.AdoptionEntity;
import com.g1appdev.Hubbits.repository.AdoptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdoptionService {

    @Autowired
    private AdoptionRepository adoptionRepository;

    // Get all adoptions
    public List<AdoptionEntity> getAllAdoptions() {
        return adoptionRepository.findAll();
    }

    // Get adoption by ID
    public Optional<AdoptionEntity> getAdoptionById(Long id) {
        return adoptionRepository.findById(id);
    }

    // Create a new adoption
    public AdoptionEntity createAdoption(AdoptionEntity adoption) {
        // Ensure all fields are properly set before saving
        return adoptionRepository.save(adoption);
    }

    // Update adoption by ID
    public AdoptionEntity updateAdoption(Long id, AdoptionEntity adoption) {
        Optional<AdoptionEntity> existingAdoptionOpt = adoptionRepository.findById(id);
        if (existingAdoptionOpt.isPresent()) {
            AdoptionEntity existingAdoption = existingAdoptionOpt.get();

            // Update the fields
            existingAdoption.setAdoptionDate(adoption.getAdoptionDate());
            existingAdoption.setStatus(adoption.getStatus());
            existingAdoption.setName(adoption.getName()); 
            existingAdoption.setAddress(adoption.getAddress());
            existingAdoption.setContactNumber(adoption.getContactNumber());
            existingAdoption.setPetType(adoption.getPetType());
            existingAdoption.setBreed(adoption.getBreed()); 
            existingAdoption.setDescription(adoption.getDescription());
            existingAdoption.setSubmissionDate(adoption.getSubmissionDate());
            existingAdoption.setPhoto(adoption.getPhoto()); 

            return adoptionRepository.save(existingAdoption);
        }
        return null;
    }

    // Delete adoption by ID
    public boolean deleteAdoption(Long id) {
        if (adoptionRepository.existsById(id)) {
            adoptionRepository.deleteById(id);
            return true;
        }
        return false; // Return false if not found
    }
}
