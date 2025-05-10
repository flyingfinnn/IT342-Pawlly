package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.LostAndFoundEntity;
import com.g1appdev.Hubbits.entity.UserEntity;
import com.g1appdev.Hubbits.repository.LostAndFoundRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class LostAndFoundService {

    @Autowired
    private LostAndFoundRepository repository;

    @Autowired
    private UserService userService;

    @Autowired
    private StorageService storageService;

    private static final String UPLOAD_DIR = "src/main/resources/static/lostfound-images";

    public String uploadImage(MultipartFile file) throws IOException {
        Path uploadPath = Path.of(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String newFilename = System.currentTimeMillis() + "-" + originalFilename;
        Path targetPath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/lostfound-images/" + newFilename;
    }

    // Create a new report
    public LostAndFoundEntity createReport(LostAndFoundEntity report, MultipartFile imageFile) {
        String email = getAuthenticatedUsername();
        Optional<UserEntity> currentUser = userService.findByEmail(email);

        if (currentUser.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        // Set the creatorId
        report.setCreatorid(Math.toIntExact(currentUser.get().getUserId()));

        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageurl = storageService.uploadToSupabase(imageFile, "lostandfound/");
                report.setImageurl(imageurl);
            } catch (Exception e) {
                throw new RuntimeException("Error saving image file", e);
            }
        }
        return repository.save(report);
    }

    // Retrieve all reports
    public List<LostAndFoundEntity> getAllReports() {
        return repository.findAll();
    }

    // Retrieve a report by ID
    public Optional<LostAndFoundEntity> getReportById(int id) {
        return repository.findById(id);
    }

    // Update an existing report
    @Transactional
    public LostAndFoundEntity updateReport(int id, LostAndFoundEntity updatedReport, MultipartFile imageFile) {
        String email = getAuthenticatedUsername();
        Optional<UserEntity> currentUser = userService.findByEmail(email);

        if (currentUser.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        UserEntity user = currentUser.get();
        boolean isAdmin = user.getRole().equalsIgnoreCase("ROLE_ADMIN");

        return repository.findById(id).map(existingReport -> {
            if (!isAdmin && existingReport.getCreatorid() != Math.toIntExact(user.getUserId())) {
                throw new IllegalArgumentException("You are not authorized to update this report.");
            }

            // Update report fields
            existingReport.setReporttype(updatedReport.getReporttype());
            existingReport.setPetname(updatedReport.getPetname());
            existingReport.setDatereported(updatedReport.getDatereported());
            existingReport.setLastseen(updatedReport.getLastseen());
            existingReport.setDescription(updatedReport.getDescription());

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageurl = uploadImage(imageFile);
                    existingReport.setImageurl(imageurl);
                } catch (IOException e) {
                    throw new RuntimeException("Error updating image file", e);
                }
            }

            return repository.save(existingReport);
        }).orElseThrow(() -> new IllegalArgumentException("Report with ID " + id + " not found"));
    }

    @Transactional
    public void deleteReport(int id) {
        String email = getAuthenticatedUsername();
        Optional<UserEntity> currentUser = userService.findByEmail(email);

        if (currentUser.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        UserEntity user = currentUser.get();
        boolean isAdmin = user.getRole().equalsIgnoreCase("ROLE_ADMIN");

        System.out.println("Authenticated user: " + email + ", Role: " + user.getRole());
        System.out.println("Attempting to delete report with ID: " + id);

        // Retrieve the report
        LostAndFoundEntity report = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Report with ID " + id + " not found."));

        System.out.println("Report found: Creator ID = " + report.getCreatorid());

        // Allow deletion if the user is the creator or an admin
        if (!isAdmin && report.getCreatorid() != Math.toIntExact(user.getUserId())) {
            throw new IllegalArgumentException("You are not authorized to delete this report.");
        }

        repository.delete(report);
        System.out.println("Report with ID " + id + " deleted successfully.");
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
            throw new IllegalArgumentException("User is not authenticated.");
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    public List<LostAndFoundEntity> getReportsByCreatorId(int creatorId) {
        return repository.findByCreatorid(creatorId);
    }
}