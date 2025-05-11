package com.g1appdev.Hubbits.service;

// import com.g1appdev.Hubbits.exception.ResourceNotFoundException; // Example import
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
    public AdoptionEntity updateAdoption(Long id, AdoptionEntity adoptionDetails) {
        Optional<AdoptionEntity> existingAdoptionOpt = adoptionRepository.findById(id);
        if (existingAdoptionOpt.isPresent()) {
            AdoptionEntity existingAdoption = existingAdoptionOpt.get();

            // Update the fields
            existingAdoption.setAdoptionDate(adoptionDetails.getAdoptionDate());
            existingAdoption.setStatus(adoptionDetails.getStatus());
            existingAdoption.setName(adoptionDetails.getName());
            existingAdoption.setAddress(adoptionDetails.getAddress());
            existingAdoption.setContactNumber(adoptionDetails.getContactNumber());
            existingAdoption.setPetType(adoptionDetails.getPetType());
            existingAdoption.setBreed(adoptionDetails.getBreed());
            existingAdoption.setDescription(adoptionDetails.getDescription());
            existingAdoption.setSubmissionDate(adoptionDetails.getSubmissionDate());
            existingAdoption.setPhoto(adoptionDetails.getPhoto());

            return adoptionRepository.save(existingAdoption);
        }
        throw new RuntimeException("Adoption not found with id: " + id); // Or a custom ResourceNotFoundException
    }

    // Delete adoption by ID
    public boolean deleteAdoption(Long id) {
        if (adoptionRepository.existsById(id)) {
            adoptionRepository.deleteById(id);
            return true;
        }
        // Consider throwing an exception if not found, for consistency with update,
        // or if the client needs to distinguish between "deleted" and "not found to delete".
        return false; 
    }
}
