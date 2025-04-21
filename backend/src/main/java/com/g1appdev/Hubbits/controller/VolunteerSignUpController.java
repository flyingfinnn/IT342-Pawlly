package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.VolunteerSignUp;
import com.g1appdev.Hubbits.service.VolunteerSignUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/volunteer")
public class VolunteerSignUpController {

    @Autowired
    private VolunteerSignUpService volunteerSignUpService;

    // Register a new volunteer for a specific opportunity
    @PostMapping("/signup/{opportunityId}")
    public ResponseEntity<VolunteerSignUp> registerVolunteer(@PathVariable int opportunityId,
            @RequestBody VolunteerSignUp volunteerSignUp) {
        try {
            // Register the volunteer
            VolunteerSignUp createdSignUp = volunteerSignUpService.registerVolunteer(opportunityId, volunteerSignUp);
            return new ResponseEntity<>(createdSignUp, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Invalid input or opportunity not found
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT); // Duplicate sign-up
        }
    }
    
    // Get all volunteer sign-ups
    @GetMapping("/signup")
    public ResponseEntity<List<VolunteerSignUp>> getAllVolunteers() {
        List<VolunteerSignUp> signUps = volunteerSignUpService.getAllSignUps();
        return new ResponseEntity<>(signUps, HttpStatus.OK);
    }

    // Get a volunteer by ID
    @GetMapping("/signup/{id}")
    public ResponseEntity<VolunteerSignUp> getVolunteerById(@PathVariable int id) {
        Optional<VolunteerSignUp> volunteerSignUp = volunteerSignUpService.getSignUpById(id);
        if (volunteerSignUp.isPresent()) {
            return new ResponseEntity<>(volunteerSignUp.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Volunteer not found
    }

    // Update a volunteer sign-up
    @PutMapping("/signup/{id}")
    public ResponseEntity<VolunteerSignUp> updateVolunteer(@PathVariable int id,
            @RequestBody VolunteerSignUp volunteerSignUp) {
        try {
            VolunteerSignUp updatedSignUp = volunteerSignUpService.updateSignUp(id, volunteerSignUp);
            return new ResponseEntity<>(updatedSignUp, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Sign-up not found
        }
    }

    // Delete a volunteer sign-up
    @DeleteMapping("/signup/{id}")
    public ResponseEntity<String> deleteVolunteer(@PathVariable int id) {
        volunteerSignUpService.deleteSignUp(id);
        return new ResponseEntity<>("Volunteer sign-up deleted successfully", HttpStatus.OK);
    }

    // Update hours worked for a volunteer sign-up
    @PatchMapping("/signup/{id}/hours")
    public ResponseEntity<VolunteerSignUp> updateHoursWorked(@PathVariable int id, @RequestParam int hoursWorked) {
        try {
            VolunteerSignUp updatedVolunteer = volunteerSignUpService.updateHoursWorked(id, hoursWorked);
            return new ResponseEntity<>(updatedVolunteer, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Volunteer sign-up not found
        }
    }
}