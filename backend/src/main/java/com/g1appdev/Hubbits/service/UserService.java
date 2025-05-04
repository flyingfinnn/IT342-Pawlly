package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.UserEntity;
import com.g1appdev.Hubbits.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.net.URL;
import org.apache.commons.io.IOUtils;

import com.g1appdev.Hubbits.model.UserOAuthResponse;

import java.io.InputStream;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username: {}", username);

        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("User not found with username: {}", username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });

        logger.info("User loaded successfully: {}", username);
        return new User(userEntity.getUsername(), userEntity.getPassword(), new ArrayList<>());
    }

    public Optional<UserEntity> findByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            logger.info("User found: {}", username);
        } else {
            logger.warn("User not found: {}", username);
        }
        return user;
    }

    // Helper method to log image type and bytes (temporarily allow all formats)
    private void logImageFormat(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        String contentType = file.getContentType();
        System.out.println("[DEBUG] Received file content type: " + contentType);
        try {
            byte[] bytes = file.getBytes();
            System.out.print("[DEBUG] First 8 bytes: ");
            for (int i = 0; i < Math.min(8, bytes.length); i++) {
                System.out.print(String.format("%02X ", bytes[i]));
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("[DEBUG] Error reading file bytes: " + e.getMessage());
        }
    }

    // Modified createUser method to accept MultipartFile
    public UserEntity createUser(UserEntity user, MultipartFile profilePicture) {
        logger.info("Creating user with username: {} and email: {}", user.getUsername(), user.getEmail());

        if (userRepository.existsByUsername(user.getUsername())) {
            String errorMessage = "Username already exists: " + user.getUsername();
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            String errorMessage = "Email already exists: " + user.getEmail();
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Handle the profile picture if provided
        if (profilePicture != null && !profilePicture.isEmpty()) {
            logImageFormat(profilePicture);
            try {
                user.setProfilePicture(profilePicture.getBytes());
            } catch (IOException e) {
                logger.error("Error setting profile picture during creation.", e);
                throw new RuntimeException("Error setting profile picture", e);
            }
        }

        UserEntity savedUser = userRepository.save(user);
        logger.info("User created successfully: {}", savedUser.getUsername());
        return savedUser;
    }

    public List<UserEntity> getAllUsers() {
        logger.info("Fetching all users.");
        List<UserEntity> users = userRepository.findAll();
        logger.info("Total users found: {}", users.size());
        return users;
    }

    public Optional<UserEntity> findUserById(Long userId) {
        logger.info("Fetching user by ID: {}", userId);
        Optional<UserEntity> user = userRepository.findById(userId);
        if (user.isPresent()) {
            logger.info("User found with ID: {}", userId);
        } else {
            logger.warn("User not found with ID: {}", userId);
        }
        return user;
    }

    public UserEntity updateUser(Long userId, UserEntity updatedUser, MultipartFile profilePicture) {
        logger.info("Updating user with ID: {}", userId);
        return userRepository.findById(userId)
                .map(existingUser -> {
                    logger.info("User found. Updating fields.");
                    if (updatedUser.getUsername() != null) {
                        logger.info("Updating username: {}", updatedUser.getUsername());
                        existingUser.setUsername(updatedUser.getUsername());
                    }
                    if (updatedUser.getFirstName() != null) {
                        logger.info("Updating first name: {}", updatedUser.getFirstName());
                        existingUser.setFirstName(updatedUser.getFirstName());
                    }
                    if (updatedUser.getLastName() != null) {
                        logger.info("Updating last name: {}", updatedUser.getLastName());
                        existingUser.setLastName(updatedUser.getLastName());
                    }
                    if (updatedUser.getEmail() != null) {
                        logger.info("Updating email: {}", updatedUser.getEmail());
                        existingUser.setEmail(updatedUser.getEmail());
                    }
                    if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                        logger.info("Updating password.");
                        existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                    }
                    if (updatedUser.getAddress() != null) {
                        logger.info("Updating address: {}", updatedUser.getAddress());
                        existingUser.setAddress(updatedUser.getAddress());
                    }
                    if (updatedUser.getPhoneNumber() != null) {
                        logger.info("Updating phone number: {}", updatedUser.getPhoneNumber());
                        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
                    }
                    if (updatedUser.getRole() != null) {
                        logger.info("Updating role: {}", updatedUser.getRole());
                        existingUser.setRole(updatedUser.getRole());
                    }
                    if (profilePicture != null && !profilePicture.isEmpty()) {
                        logImageFormat(profilePicture);
                        try {
                            logger.info("Updating profile picture.");
                            existingUser.setProfilePicture(profilePicture.getBytes());
                        } catch (IOException e) {
                            logger.error("Error setting profile picture.", e);
                            throw new RuntimeException("Error setting profile picture", e);
                        }
                    }
                    UserEntity savedUser = userRepository.save(existingUser);
                    logger.info("User updated successfully: {}", savedUser.getUsername());
                    return savedUser;
                })
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", userId);
                    return new RuntimeException("User not found");
                });
    }

    public boolean deleteUser(Long userId) {
        logger.info("Deleting user with ID: {}", userId);
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            logger.info("User deleted successfully.");
            return true;
        } else {
            logger.warn("User not found for deletion: {}", userId);
            return false;
        }
    }

    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        logger.info("Changing password for user ID: {}", userId);
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            if (passwordEncoder.matches(oldPassword, user.getPassword())) {
                logger.info("Old password matches. Updating password.");
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                logger.info("Password updated successfully.");
                return true;
            } else {
                logger.warn("Old password does not match.");
            }
        } else {
            logger.warn("User not found with ID: {}", userId);
        }
        return false;
    }

    public boolean isOwnerOrAdmin(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentPrincipal = authentication.getName();
        // Try by email first, then by username
        Optional<UserEntity> currentUser = userRepository.findByEmail(currentPrincipal);
        if (currentUser.isEmpty()) {
            currentUser = userRepository.findByUsername(currentPrincipal);
        }
        boolean isOwner = currentUser.isPresent() && currentUser.get().getUserId().equals(userId);
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        logger.info("Checking ownership/admin rights. IsOwner: {}, IsAdmin: {}", isOwner, isAdmin);
        return isOwner || isAdmin;
    }

    public boolean existsByUsername(String username) {
        boolean exists = userRepository.existsByUsername(username);
        logger.info("Username {} exists: {}", username, exists);
        return exists;
    }

    public boolean existsByEmail(String email) {
        boolean exists = userRepository.existsByEmail(email);
        logger.info("Email {} exists: {}", email, exists);
        return exists;
    }

    public Optional<UserEntity> findByEmail(String email) {
        logger.info("Fetching user by email: {}", email);
        Optional<UserEntity> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            logger.info("User found: {}", email);
        } else {
            logger.warn("User not found: {}", email);
        }
        return user;
    }

    public UserOAuthResponse loginOrCreateGoogleUser(String googleId, String email, String name, String pictureUrl) {
        logger.info("Google OAuth: googleId={}, email={}, name={}, pictureUrl={}", googleId, email, name, pictureUrl);
        Optional<UserEntity> userOpt = userRepository.findByGoogleId(googleId);
        UserEntity user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
            logger.info("Existing Google user found: userId={}, googleId={}", user.getUserId(), user.getGoogleId());
        } else {
            user = new UserEntity();
            user.setGoogleId(googleId);
            logger.info("Set googleId: {}", googleId);
            user.setEmail(email);
            // Set username to the part before '@' in the email, robustly
            String username = "";
            if (email != null && email.contains("@")) {
                username = email.substring(0, email.indexOf('@'));
            }
            user.setUsername(username);
            String[] nameParts = name != null ? name.split(" ", 2) : new String[]{"", ""};
            user.setFirstName(nameParts[0]);
            user.setLastName(nameParts.length > 1 ? nameParts[1] : "");
            // Download and store profile picture as bytea (robust version)
            try {
                if (pictureUrl != null && !pictureUrl.isEmpty()) {
                    logger.info("Attempting to download Google profile picture from: {}", pictureUrl);
                    URL url = new URL(pictureUrl);
                    try (InputStream in = url.openStream()) {
                        byte[] imageBytes = org.apache.commons.io.IOUtils.toByteArray(in);
                        user.setProfilePicture(imageBytes);
                        logger.info("Downloaded profile picture, size: {} bytes", imageBytes.length);
                    }
                } else {
                    logger.warn("No pictureUrl provided from Google account.");
                }
            } catch (Exception e) {
                logger.warn("Failed to download Google profile picture: {}", e.getMessage());
            }
            user.setPassword(null); // No password for OAuth users
            user = userRepository.save(user);
            logger.info("Saved new Google OAuth user: userId={}, googleId={}", user.getUserId(), user.getGoogleId());
        }
        UserOAuthResponse resp = new UserOAuthResponse();
        resp.setUserId(user.getUserId());
        resp.setGoogleId(user.getGoogleId());
        resp.setEmail(user.getEmail());
        resp.setName(user.getFirstName() + (user.getLastName() != null ? (" " + user.getLastName()) : ""));
        resp.setProfilePicture(user.getProfilePictureBase64());
        logger.info("Returning OAuth response: userId={}, googleId={}, profilePictureNull={}", resp.getUserId(), resp.getGoogleId(), resp.getProfilePicture() == null);
        return resp;
    }
}