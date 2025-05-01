package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.UserEntity;
import com.g1appdev.Hubbits.repository.UserRepository;
import com.g1appdev.Hubbits.service.MicrosoftGraphService;
import com.g1appdev.Hubbits.service.MicrosoftUser;
import com.g1appdev.Hubbits.service.UserService;
import com.g1appdev.Hubbits.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import java.util.Map;
import java.util.Optional;
import java.util.List;

@CrossOrigin(origins = "${FRONTEND_URL}")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MicrosoftGraphService microsoftGraphService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtTokenUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/signup")
    public ResponseEntity<String> signupUser(@RequestBody UserEntity newUser) {
        // Log the incoming request for debugging purposes
        logger.info(
                "Signup request received: FirstName={}, LastName={}, Username={}, Email={}, Address={}, PhoneNumber={}",
                newUser.getFirstName(), newUser.getLastName(), newUser.getUsername(), newUser.getEmail(),
                newUser.getAddress(), newUser.getPhoneNumber());

        try {

            newUser.setRole("ROLE_USER");

            userService.createUser(newUser);

            logger.info("User successfully saved: Username={}", newUser.getUsername());
            return ResponseEntity.ok("User saved successfully");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {

            logger.error("Error during signup", e);
            return ResponseEntity.status(500).body("Error during user registration: " + e.getMessage());
        }
    }
    @PostMapping("/microsoft-login")
    public ResponseEntity<?> microsoftLogin(@RequestBody Map<String, String> payload) {
    String token = payload.get("token");

    try {
        // Validate the token with Microsoft
        MicrosoftUser microsoftUser = microsoftGraphService.validateTokenAndGetUser(token);

        if (microsoftUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Microsoft token");
        }

        // Check if the user already exists
        Optional<UserEntity> existingUser = userService.findByEmail(microsoftUser.getEmail());
        UserEntity user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            // Create a new user if not found
            user = new UserEntity();
            user.setFirstName(microsoftUser.getFirstName());
            user.setLastName(microsoftUser.getLastName());
            user.setEmail(microsoftUser.getEmail());
            user.setUsername(microsoftUser.getEmail()); // Use email as username
            user.setRole("ROLE_USER"); // Default role for new users
            user.setPassword("defaultPassword"); // Placeholder password (will be encoded in createUser)

            user = userService.createUser(user); // Reuse the existing createUser method
        }

        // Generate a JWT for the user
        String jwt = userService.generateTokenForUser(user);

        return ResponseEntity.ok(Map.of("token", jwt, "user", user));
        } catch (Exception e) {
        logger.error("Error during Microsoft login", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during Microsoft login");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody UserEntity loginRequest) {
        logger.info("Login attempt with username: {}", loginRequest.getUsername());

        Optional<UserEntity> userEntity = userService.findByUsername(loginRequest.getUsername());

        logger.info("Incoming password: {}", loginRequest.getPassword());
        logger.info("Stored password (hashed): {}", userEntity.get().getPassword());

        if (!passwordEncoder.matches(loginRequest.getPassword(), userEntity.get().getPassword())) {
            logger.error("Passwords do NOT match!");
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        logger.info("Passwords match!");

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(auth);
            logger.info("User {} authenticated successfully", loginRequest.getUsername());
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(401).body("Invalid credentials");
        }

        List<String> roles = List.of(userEntity.get().getRole().startsWith("ROLE_") ? userEntity.get().getRole()
                : "ROLE_" + userEntity.get().getRole());

        logger.info("Roles for token generation: {}", roles);

        String token = jwtTokenUtil.generateToken(loginRequest.getUsername(), roles);
        return ResponseEntity.ok(token);
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

}