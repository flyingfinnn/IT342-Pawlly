package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.VolunteerOpportunity;
import com.g1appdev.Hubbits.service.VolunteerOpportunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerOpportunityController {

    @Autowired
    private VolunteerOpportunityService service;

    // Get all opportunities
    @GetMapping("/opportunities")
    public ResponseEntity<List<VolunteerOpportunity>> viewOpportunities() {
        List<VolunteerOpportunity> opportunities = service.getAllOpportunities();
        return ResponseEntity.ok(opportunities);
    }

    // Get a single opportunity by ID, including the number of sign-ups
    @GetMapping("/opportunity/{id}")
    public ResponseEntity<VolunteerOpportunity> getOpportunityWithSignUps(@PathVariable int id) {
        Optional<VolunteerOpportunity> opportunity = service.getOpportunityById(id);
        return opportunity
                .map(ResponseEntity::ok) 
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null)); // Return null body with status
    }

    // Get the number of sign-ups for a specific opportunity
    @GetMapping("/opportunity/{id}/signups")
    public ResponseEntity<Integer> getNumberOfSignUps(@PathVariable int id) {
        try {
            int signUpCount = service.getNumberOfSignUps(id);
            return ResponseEntity.ok(signUpCount);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return null body with status
        }
    }

    // Create a new opportunity
    @PostMapping("/opportunity")
    public ResponseEntity<VolunteerOpportunity> createOpportunity(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("registrationStartDate") String registrationStartDate,
            @RequestParam("registrationEndDate") String registrationEndDate,
            @RequestParam("volunteerDatetime") String volunteerDatetime,
            @RequestParam("location") String location,
            @RequestParam("hoursWorked") int hoursWorked,
            @RequestParam("volunteersNeeded") int volunteersNeeded,
            @RequestParam("creatorId") int creatorId, 
            @RequestParam(value = "volunteerImage", required = false) MultipartFile volunteerImage) {

        // Validate dates and other required fields here

        LocalDateTime regStart = LocalDateTime.parse(registrationStartDate);
        LocalDateTime regEnd = LocalDateTime.parse(registrationEndDate);
        LocalDateTime volunteerDate = LocalDateTime.parse(volunteerDatetime);

        VolunteerOpportunity opportunity = new VolunteerOpportunity();
        opportunity.setTitle(title);
        opportunity.setDescription(description);
        opportunity.setRegistrationStartDate(regStart);
        opportunity.setRegistrationEndDate(regEnd);
        opportunity.setVolunteerDatetime(volunteerDate);
        opportunity.setLocation(location);
        opportunity.setHoursWorked(hoursWorked);
        opportunity.setVolunteersNeeded(volunteersNeeded);

        // Handle image upload
        if (volunteerImage != null && !volunteerImage.isEmpty()) {
            try {
                String imageUrl = service.uploadImage(volunteerImage);
                opportunity.setVolunteerImageUrl(imageUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }

        VolunteerOpportunity createdOpportunity = service.createOpportunity(opportunity, volunteerImage, creatorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOpportunity);
    }

    // Update an existing opportunity, including volunteerImageUrl
    @PutMapping("/opportunity/{id}")
    public ResponseEntity<VolunteerOpportunity> updateOpportunity(
            @PathVariable int id,
            @RequestPart("opportunity") VolunteerOpportunity opportunity,
            @RequestParam(value = "volunteerImage", required = false) MultipartFile volunteerImage) {
    
        try {
            // Update the opportunity in the service
            VolunteerOpportunity updatedOpportunity = service.updateOpportunity(id, opportunity, volunteerImage);
            return ResponseEntity.ok(updatedOpportunity);
        } catch (IllegalArgumentException e) {
            // Opportunity with the provided ID not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (Exception e) {
            // Handle unexpected errors
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Delete an opportunity
    @DeleteMapping("/opportunity/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable int id) {
        try {
            service.deleteOpportunity(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
