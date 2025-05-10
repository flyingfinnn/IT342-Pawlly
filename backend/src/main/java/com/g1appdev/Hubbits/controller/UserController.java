package com.g1appdev.Hubbits.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.g1appdev.Hubbits.entity.UserEntity;
import com.g1appdev.Hubbits.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@CrossOrigin(origins = "https://pawlly-mobilebackend.onrender.com")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    // Create a new user
    @PostMapping
    public ResponseEntity<UserEntity> createUser(
            @RequestPart("user") String userJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        try {
            logger.info("Received signup request: userJson={} | profilePicture present: {} | profilePicture size: {}", userJson, (profilePicture != null), (profilePicture != null ? profilePicture.getSize() : 0));
            UserEntity user = new ObjectMapper().readValue(userJson, UserEntity.class);
            UserEntity createdUser = userService.createUser(user, profilePicture); // Adjusted service call
            logger.info("User created: {}", createdUser);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Error in createUser: ", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
                    "profilePicture", foundUser.getProfilePictureBase64()
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // Update a user by ID with profile picture support
    @PutMapping("/{id}")
    public ResponseEntity<UserEntity> updateUser(
            @PathVariable Long id,
            @RequestPart("user") String userJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {

        if (userService.isOwnerOrAdmin(id)) {
            try {
                UserEntity updatedUser = new ObjectMapper().readValue(userJson, UserEntity.class);
                UserEntity user = userService.updateUser(id, updatedUser, profilePicture); // Adjusted service call
                if (user != null) {
                    return new ResponseEntity<>(user, HttpStatus.OK);
                }
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
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
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication != null ? authentication.getName() : null;

        if (currentEmail == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        Optional<UserEntity> user = userService.findByEmail(currentEmail);
        if (user.isPresent()) {
            UserEntity foundUser = user.get();
            String profilePictureBase64 = foundUser.getProfilePicture() != null
                    ? Base64.getEncoder().encodeToString(foundUser.getProfilePicture())
                    : "";
            Map<String, Object> response = Map.of(
                    "userId", foundUser.getUserId(),
                    "username", foundUser.getUsername() != null ? foundUser.getUsername() : "",
                    "firstName", foundUser.getFirstName() != null ? foundUser.getFirstName() : "",
                    "lastName", foundUser.getLastName() != null ? foundUser.getLastName() : "",
                    "email", foundUser.getEmail() != null ? foundUser.getEmail() : "",
                    "address", foundUser.getAddress() != null ? foundUser.getAddress() : "",
                    "phoneNumber", foundUser.getPhoneNumber() != null ? foundUser.getPhoneNumber() : "",
                    "role", foundUser.getRole() != null ? foundUser.getRole() : "",
                    "profilePicture", profilePictureBase64 != null ? profilePictureBase64 : "",
                    "googleId", foundUser.getGoogleId() != null ? foundUser.getGoogleId() : ""
            );
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
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