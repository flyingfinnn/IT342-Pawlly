package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.VolunteerOpportunity;
import com.g1appdev.Hubbits.entity.VolunteerSignUp;
import com.g1appdev.Hubbits.repository.VolunteerOpportunityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VolunteerOpportunityService {

    @Autowired
    private VolunteerOpportunityRepository opportunityRepository;

    private static final String UPLOAD_DIR = "src\\main\\resources\\images";  // Define folder to store images in the backend

    // Upload image method
    public String uploadImage(MultipartFile file) throws IOException {
        // Create the directory if it doesn't exist
        Path uploadPath = Path.of(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);  // Create directories if they don't exist
        }

        // Generate a unique filename to avoid conflicts
        String originalFilename = file.getOriginalFilename();
        String newFilename = System.currentTimeMillis() + "-" + originalFilename;

        // Define the target path where the file will be saved
        Path targetPath = uploadPath.resolve(newFilename);

        // Save the file to the local storage
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Return the relative path to the image (URL can be modified if necessary)
        return "/images/" + newFilename;  // Frontend can access the image via this URL
    }

    // Create or save a new volunteer opportunity
    public VolunteerOpportunity createOpportunity(VolunteerOpportunity opportunity, MultipartFile volunteerImage, int creatorId) {
        if (volunteerImage != null && !volunteerImage.isEmpty()) {
            try {
                // Upload the image and retrieve the URL
                String imageUrl = uploadImage(volunteerImage);
                opportunity.setVolunteerImageUrl(imageUrl); // Set the image URL (relative path to the saved image)
            } catch (IOException e) {
                throw new RuntimeException("Error processing volunteer image", e);
            }
        }

        // Set registration period default dates if not provided
        if (opportunity.getRegistrationStartDate() == null) {
            opportunity.setRegistrationStartDate(LocalDateTime.now());  // Set current datetime as default registration start date
        }
        if (opportunity.getRegistrationEndDate() == null) {
            opportunity.setRegistrationEndDate(opportunity.getRegistrationStartDate().plusDays(5));  // Default 5 days for registration
        }

        // Set the volunteer event date if not provided
        if (opportunity.getVolunteerDatetime() == null) {
            throw new IllegalArgumentException("Volunteer event date (volunteerDatetime) is required.");
        }

        // Set the creator ID before saving the opportunity
        opportunity.setCreatorId(creatorId);

        return opportunityRepository.save(opportunity);
    }

    // Retrieve all volunteer opportunities
    public List<VolunteerOpportunity> getAllOpportunities() {
        return opportunityRepository.findAll();
    }

    // Retrieve a volunteer opportunity by its ID (returns Optional)
    public Optional<VolunteerOpportunity> getOpportunityById(int id) {
        return opportunityRepository.findById(id);
    }

    @Transactional
    public VolunteerOpportunity updateOpportunity(int id, VolunteerOpportunity updatedOpportunity, MultipartFile volunteerImage) {
        return opportunityRepository.findById(id)
                .map(existingOpportunity -> {
                    // Update existing fields only if they're not null or empty
                    if (updatedOpportunity.getTitle() != null) {
                        existingOpportunity.setTitle(updatedOpportunity.getTitle());
                    }
                    if (updatedOpportunity.getDescription() != null) {
                        existingOpportunity.setDescription(updatedOpportunity.getDescription());
                    }
                    if (updatedOpportunity.getRegistrationStartDate() != null) {
                        existingOpportunity.setRegistrationStartDate(updatedOpportunity.getRegistrationStartDate());
                    }
                    if (updatedOpportunity.getRegistrationEndDate() != null) {
                        existingOpportunity.setRegistrationEndDate(updatedOpportunity.getRegistrationEndDate());
                    }
                    if (updatedOpportunity.getVolunteerDatetime() != null) {
                        existingOpportunity.setVolunteerDatetime(updatedOpportunity.getVolunteerDatetime());
                    }
                    if (updatedOpportunity.getLocation() != null) {
                        existingOpportunity.setLocation(updatedOpportunity.getLocation());
                    }
                    if (updatedOpportunity.getHoursWorked() > 0) {
                        existingOpportunity.setHoursWorked(updatedOpportunity.getHoursWorked());
                    }
                    if (updatedOpportunity.getVolunteersNeeded() > 0) {
                        existingOpportunity.setVolunteersNeeded(updatedOpportunity.getVolunteersNeeded());
                    }
    
                    // Update the volunteer image URL if a new image is provided
                    if (volunteerImage != null && !volunteerImage.isEmpty()) {
                        try {
                            // Upload the image and retrieve the URL
                            String imageUrl = uploadImage(volunteerImage);
                            existingOpportunity.setVolunteerImageUrl(imageUrl); // Set the image URL
                        } catch (IOException e) {
                            throw new RuntimeException("Error processing volunteer image", e);
                        }
                    }
    
                    // The creator_id remains unchanged, so we don't need to modify it.
                    // The creator_id will automatically remain the same as in the database.
                    // You don't need to set it again unless it should be updated (which doesn't seem to be the case here).
    
                    return opportunityRepository.save(existingOpportunity);
                })
                .orElseThrow(() -> new IllegalArgumentException("Opportunity with ID " + id + " not found"));
    }
    

    // Delete a volunteer opportunity by its ID
    public String deleteOpportunity(int id) {
        if (opportunityRepository.existsById(id)) {
            opportunityRepository.deleteById(id);
            return "Opportunity with ID " + id + " deleted successfully";
        } else {
            throw new IllegalArgumentException("Opportunity with ID " + id + " not found");
        }
    }

    // Get all volunteer sign-ups for a specific opportunity
    public List<VolunteerSignUp> getSignUpsForOpportunity(int opportunityId) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity with ID " + opportunityId + " not found"));
        return opportunity.getVolunteerSignUps();
    }

    // Get the number of sign-ups for a specific opportunity
    public int getNumberOfSignUps(int opportunityId) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity with ID " + opportunityId + " not found"));
        return opportunity.getNumberOfSignUps();  // This uses the getter from VolunteerOpportunity to count sign-ups
    }
}
