package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.VolunteerOpportunity;
import com.g1appdev.Hubbits.entity.VolunteerSignUp;
import com.g1appdev.Hubbits.repository.VolunteerOpportunityRepository;
import com.g1appdev.Hubbits.repository.VolunteerSignUpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class VolunteerSignUpService {

    @Autowired
    private VolunteerSignUpRepository signUpRepository;

    @Autowired
    private VolunteerOpportunityRepository opportunityRepository;

    // Register a volunteer for a specific opportunity
    public VolunteerSignUp registerVolunteer(int opportunityId, VolunteerSignUp signUp) {
        // Validate input data
        validateSignUpData(signUp);

        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity with ID " + opportunityId + " not found"));

        // Check for duplicate sign-up
        if (signUpRepository.existsByEmailAndVolunteerOpportunity_OpportunityID(signUp.getEmail(), opportunityId)) {
            throw new IllegalStateException("Volunteer with email " + signUp.getEmail() + 
                    " is already signed up for opportunity ID " + opportunityId);
        }

        signUp.setVolunteerOpportunity(opportunity); // Set the associated opportunity
        signUp.setDateRegistered(LocalDateTime.now()); // Automatically set the registration date
        return signUpRepository.save(signUp);
    }
    

    // Retrieve all sign-ups
    public List<VolunteerSignUp> getAllSignUps() {
        return signUpRepository.findAll();
    }

    // Retrieve a specific sign-up by ID
    public Optional<VolunteerSignUp> getSignUpById(int signUpId) {
        return signUpRepository.findById(signUpId);
    }

    // Update volunteer sign-up details
    @Transactional
    public VolunteerSignUp updateSignUp(int signUpId, VolunteerSignUp updatedSignUp) {
        return signUpRepository.findById(signUpId)
            .map(signUp -> {
                // Update regular fields
                signUp.setFirstName(updatedSignUp.getFirstName());
                signUp.setLastName(updatedSignUp.getLastName());
                signUp.setEmail(updatedSignUp.getEmail());
                signUp.setAddress(updatedSignUp.getAddress());
                signUp.setPhoneNumber(updatedSignUp.getPhoneNumber());

                // Update hoursWorked
                signUp.setHoursWorked(updatedSignUp.getHoursWorked());

                // Save the updated sign-up object
                return signUpRepository.save(signUp);
            })
            .orElseThrow(() -> new IllegalArgumentException("Sign-up with ID " + signUpId + " not found"));
    }

    // Delete a sign-up by ID
    public void deleteSignUp(int signUpId) {
        signUpRepository.deleteById(signUpId);
    }

    // Update hours worked for a volunteer sign-up
    @Transactional
    public VolunteerSignUp updateHoursWorked(int signUpId, int hoursWorked) {
        return signUpRepository.findById(signUpId)
                .map(signUp -> {
                    signUp.setHoursWorked(hoursWorked); // Update hours worked
                    return signUpRepository.save(signUp);
                })
                .orElseThrow(() -> new IllegalArgumentException("Sign-up with ID " + signUpId + " not found"));
    }

    // Get all sign-ups for a specific opportunity
    public List<VolunteerSignUp> getSignUpsByOpportunity(int opportunityId) {
        VolunteerOpportunity opportunity = opportunityRepository.findById(opportunityId)
                .orElseThrow(() -> new IllegalArgumentException("Opportunity with ID " + opportunityId + " not found"));
        return opportunity.getVolunteerSignUps();
    }

    // Helper method to validate VolunteerSignUp data
    private void validateSignUpData(VolunteerSignUp signUp) {
        if (signUp.getFirstName() == null || signUp.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (signUp.getLastName() == null || signUp.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (signUp.getEmail() == null || signUp.getEmail().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (signUp.getPhoneNumber() == null || signUp.getPhoneNumber().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
    }
}