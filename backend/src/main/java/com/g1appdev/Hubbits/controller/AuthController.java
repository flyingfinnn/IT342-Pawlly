package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.UserEntity;
import com.g1appdev.Hubbits.repository.UserRepository;
import com.g1appdev.Hubbits.service.UserService;
import com.g1appdev.Hubbits.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper; // Add this import

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile; // Add this import

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping(value = "/signup", consumes = {"multipart/form-data"}) // Important: consumes attribute
    public ResponseEntity<String> signupUser(
            @RequestPart("user") String userJson,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) { // Changed to RequestPart

        try {
            UserEntity newUser = new ObjectMapper().readValue(userJson, UserEntity.class);

            // Log the incoming request for debugging purposes
            logger.info(
                    "Signup request received: FirstName={}, LastName={}, Username={}, Email={}, Address={}, PhoneNumber={}",
                    newUser.getFirstName(), newUser.getLastName(), newUser.getUsername(), newUser.getEmail(),
                    newUser.getAddress(), newUser.getPhoneNumber());

            newUser.setRole("ROLE_USER");

            userService.createUser(newUser, profilePicture); // Now passing profilePicture

            logger.info("User successfully saved: Username={}", newUser.getUsername());
            return ResponseEntity.ok("User saved successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {

            logger.error("Error during signup", e);
            return ResponseEntity.status(500).body("Error during user registration: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserEntity loginRequest) {
        logger.info("Login attempt with email: {}", loginRequest.getEmail());
        
        try {
            // Only check by email
            Optional<UserEntity> userEntity = userService.findByEmail(loginRequest.getEmail());
            if (userEntity.isEmpty()) {
                logger.error("User not found for email: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            logger.info("Incoming password: {}", loginRequest.getPassword());
            logger.info("Stored password (hashed): {}", userEntity.get().getPassword());
            
            if (passwordEncoder.matches(loginRequest.getPassword(), userEntity.get().getPassword())) {
                logger.info("Password match successful for user: {}", loginRequest.getEmail());
                String role = userEntity.get().getRole();
                if (role == null) {
                    role = "ROLE_USER";
                    userEntity.get().setRole(role);
                    userService.updateUser(userEntity.get().getUserId(), userEntity.get(), null);
                }
                String token = jwtTokenUtil.generateToken(loginRequest.getEmail(), List.of(role));
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                logger.error("Password mismatch for user: {}", loginRequest.getEmail());
                return ResponseEntity.status(401).body("Invalid credentials");
            }
        } catch (Exception e) {
            logger.error("Error during login: ", e);
            return ResponseEntity.status(500).body("Server error during login: " + e.getMessage());
        }
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/test")
    public String testPage() {
        return "test";
    }

    @GetMapping("/check-username")
    public ResponseEntity<Boolean> checkUsername(@RequestParam String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmail(@RequestParam String email) {
        boolean exists = userService.existsByEmail(email); // Check if email exists
        return ResponseEntity.ok(exists); // Return true if exists, false otherwise
    }

    @PostMapping("/oauth/google")
    public ResponseEntity<?> googleSignIn(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            String googleId = request.get("googleId");
            
            logger.info("Google sign-in attempt for email: {}", email);
            
            if (email == null || name == null) {
                logger.error("Missing required fields in Google sign-in request");
                return ResponseEntity.badRequest().body("Missing required fields");
            }
            
            // Check if user exists with this email
            Optional<UserEntity> existingUser = userService.findByEmail(email);
            
            if (existingUser.isPresent()) {
                logger.info("Existing user found for Google sign-in: {}", email);
                String token = jwtTokenUtil.generateToken(email, List.of(existingUser.get().getRole()));
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                logger.info("Creating new user for Google sign-in: {}", email);
                // Create new user
                UserEntity newUser = new UserEntity();
                newUser.setEmail(email);
                newUser.setUsername(email); // Use email as username
                newUser.setFirstName(name.split(" ")[0]);
                newUser.setLastName(name.split(" ").length > 1 ? name.split(" ")[1] : "");
                newUser.setRole("ROLE_USER");
                
                // Generate a random password for the account
                String randomPassword = UUID.randomUUID().toString();
                newUser.setPassword(passwordEncoder.encode(randomPassword));
                
                try {
                    userService.createUser(newUser, null);
                    logger.info("Successfully created new user for Google sign-in: {}", email);
                } catch (Exception e) {
                    logger.error("Error creating new user for Google sign-in: ", e);
                    return ResponseEntity.status(500).body("Error creating user: " + e.getMessage());
                }
                
                String token = jwtTokenUtil.generateToken(email, List.of("ROLE_USER"));
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                return ResponseEntity.ok(response);
            }
        } catch (Exception e) {
            logger.error("Error during Google sign-in: ", e);
            return ResponseEntity.status(500).body("Server error during Google sign-in: " + e.getMessage());
        }
    }
}