package com.g1appdev.Hubbits.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.g1appdev.Hubbits.entity.UserEntity;
import com.g1appdev.Hubbits.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "${FRONTEND_URL}")
public class UserController {

    @Autowired
    private UserService userService;

    // Create a new user
    @PostMapping
    public ResponseEntity<UserEntity> createUser(@RequestBody UserEntity user) {
        UserEntity createdUser = userService.createUser(user);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // Get all users
    @GetMapping
    public ResponseEntity<List<UserEntity>> getAllUsers() {
        List<UserEntity> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Optional<UserEntity> user = userService.findUserById(id);
        if (user.isPresent()) {
            UserEntity foundUser = user.get();
            Map<String, Object> response = Map.of(
                    "userId", foundUser.getUserId(),
                    "username", foundUser.getUsername(),
                    "firstName", foundUser.getFirstName(),
                    "lastName", foundUser.getLastName(),
                    "email", foundUser.getEmail(),
                    "address", foundUser.getAddress(),
                    "phoneNumber", foundUser.getPhoneNumber(),
                    "role", foundUser.getRole(),
                    "profilePicture", foundUser.getProfilePictureBase64() // Send Base64 encoded profile picture
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update a user by ID with profile picture support
    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')") or  @com.g1appdev.Hubbits.service.UserService.isOwner(#id)")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id,
            @RequestParam("user") String userJson,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {

        try {
            System.out.println("Received userJson: " + userJson);
            if (profilePicture != null) {
                System.out.println("Received profilePicture: " + profilePicture.getOriginalFilename());
            }

            // Deserialize user JSON
            UserEntity updatedUser = new ObjectMapper().readValue(userJson, UserEntity.class);

            // Perform update
            UserEntity user = userService.updateUser(id, updatedUser, profilePicture);

            if (user != null) {
                Map<String, Object> response = Map.of(
                        "updatedUser", user,
                        "newToken", userService.generateTokenForUser(user) // Generate token if needed
                );
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

            return new ResponseEntity<>(Map.of("error", "User not found"), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        try {
            boolean isDeleted = userService.deleteUser(id);
            if (isDeleted) {
                return ResponseEntity.ok("User deleted successfully");
            } else {
                return ResponseEntity.status(404).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error deleting user");
        }
    }


    @GetMapping("/me")
public ResponseEntity<?> getCurrentUser() {
    try {
        System.out.println("Processing /me request...");

        // Step 1: Extract authentication from SecurityContext
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        // Step 2: Extract username from authentication
        String currentUsername = authentication.getName();
        System.out.println("Extracted username: " + currentUsername);

        // Step 3: Fetch user details from the database
        System.out.println("Fetching user details for username: " + currentUsername);
        Optional<UserEntity> user = userService.findByUsername(currentUsername);

        if (user.isPresent()) {
            UserEntity foundUser = user.get();
            System.out.println("User found in database: " + foundUser.getUsername());

            // Step 4: Convert profile picture to Base64 if it exists
            String profilePictureBase64 = foundUser.getProfilePicture() != null
                    ? Base64.getEncoder().encodeToString(foundUser.getProfilePicture())
                    : "";
            System.out.println("Profile picture processed successfully");

            // Step 5: Build the response with null-safe values
            Map<String, Object> response = Map.of(
                    "userId", foundUser.getUserId() != null ? foundUser.getUserId() : -1,
                    "username", foundUser.getUsername() != null ? foundUser.getUsername() : "N/A",
                    "firstName", foundUser.getFirstName() != null ? foundUser.getFirstName() : "N/A",
                    "lastName", foundUser.getLastName() != null ? foundUser.getLastName() : "N/A",
                    "email", foundUser.getEmail() != null ? foundUser.getEmail() : "N/A",
                    "address", foundUser.getAddress() != null ? foundUser.getAddress() : "N/A",
                    "phoneNumber", foundUser.getPhoneNumber() != null ? foundUser.getPhoneNumber() : "N/A",
                    "role", foundUser.getRole() != null ? foundUser.getRole() : "N/A",
                    "profilePicture", profilePictureBase64
            );
            System.out.println("Response built successfully: " + response);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            System.out.println("User not found in the database for username: " + currentUsername);
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    } catch (Exception e) {
        System.out.println("Error occurred while processing /me request: " + e.getMessage());
        e.printStackTrace();
        return new ResponseEntity<>("An error occurred while fetching user details", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}


    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> passwordData) {
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication != null ? authentication.getName() : null;

        if (currentUsername == null) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Optional<UserEntity> userOptional = userService.findByUsername(currentUsername);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            boolean success = userService.changePassword(user.getUserId(), oldPassword, newPassword);
            if (success) {
                return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Old password is incorrect", HttpStatus.BAD_REQUEST);
            }
        }

        return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
    }
}