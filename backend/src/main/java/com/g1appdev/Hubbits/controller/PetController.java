package com.g1appdev.Hubbits.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import com.g1appdev.Hubbits.entity.PetEntity;
import com.g1appdev.Hubbits.service.PetService;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import org.springframework.beans.factory.annotation.Value;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.*;

@RestController
@RequestMapping("/api/pet")
public class PetController {
    @Autowired
    PetService pserv;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @GetMapping("/test")
    public String print(){
        return "Hello, John Edward Selma";
    }

    @PostMapping(value = "/postpetrecord", consumes = "multipart/form-data")
    public ResponseEntity<?> postPetRecord(@RequestParam("name") String name,
                                   @RequestParam("type") String type,
                                   @RequestParam("breed") String breed,
                                   @RequestParam("age") int age,
                                   @RequestParam("gender") String gender,
                                   @RequestParam("description") String description,
                                   @RequestParam(value = "photo1", required = false) MultipartFile photo1,
                                   @RequestParam(value = "photo2", required = false) MultipartFile photo2,
                                   @RequestParam(value = "photo3", required = false) MultipartFile photo3,
                                   @RequestParam(value = "photo4", required = false) MultipartFile photo4,
                                   @RequestParam("status") String status,
                                   @RequestParam("userName") String userName,
                                   @RequestParam("address") String address,
                                   @RequestParam("contactNumber") String contactNumber,
                                   @RequestParam(value = "weight", required = false) String weight,
                                   @RequestParam(value = "color", required = false) String color,
                                   @RequestParam(value = "height", required = false) String height) {
        try {
            // At least one photo is required
            if ((photo1 == null || photo1.isEmpty()) && (photo2 == null || photo2.isEmpty()) && (photo3 == null || photo3.isEmpty()) && (photo4 == null || photo4.isEmpty())) {
                return ResponseEntity.badRequest().body("At least one photo is required for pet rehome records.");
            }

            // Upload photos to Supabase and get URLs
            String photo1Url = (photo1 != null && !photo1.isEmpty()) ? uploadToSupabase(photo1, "photo1-") : null;
            String photo2Url = (photo2 != null && !photo2.isEmpty()) ? uploadToSupabase(photo2, "photo2-") : null;
            String photo3Url = (photo3 != null && !photo3.isEmpty()) ? uploadToSupabase(photo3, "photo3-") : null;
            String photo4Url = (photo4 != null && !photo4.isEmpty()) ? uploadToSupabase(photo4, "photo4-") : null;

            // Generate and upload thumbnail for photo1
            String photo1ThumbUrl = null;
            if (photo1 != null && !photo1.isEmpty()) {
                MultipartFile thumbFile = generateThumbnail(photo1, 200, 200);
                photo1ThumbUrl = uploadToSupabase(thumbFile, "photo1_thumb-");
            }

            // Get current date and time for submission_date
            String submissionDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

            // Create and save PetEntity (leave 'photo' field empty)
            PetEntity pet = new PetEntity();
            pet.setName(name);
            pet.setType(type);
            pet.setBreed(breed);
            pet.setAge(age);
            pet.setGender(gender);
            pet.setDescription(description);
            pet.setPhoto(""); // or null if you prefer
            pet.setPhoto1(photo1Url);
            pet.setPhoto2(photo2Url);
            pet.setPhoto3(photo3Url);
            pet.setPhoto4(photo4Url);
            pet.setPhoto1Thumb(photo1ThumbUrl);
            pet.setStatus(status);
            pet.setUserName(userName);
            pet.setAddress(address);
            pet.setContactNumber(contactNumber);
            pet.setWeight(weight);
            pet.setColor(color);
            pet.setHeight(height);
            pet.setSubmissionDate(submissionDate);
            PetEntity savedPet = pserv.postPetRecord(pet);
            return ResponseEntity.ok(savedPet);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error saving pet record: " + e.getMessage());
        }
    }

    // Helper function to upload to Supabase Storage
    private String uploadToSupabase(MultipartFile file, String prefix) throws Exception {
        String bucket = "petimage";
        String fileName = prefix + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucket + "/" + fileName;
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(uploadUrl))
            .header("apikey", supabaseKey)
            .header("Authorization", "Bearer " + supabaseKey)
            .header("Content-Type", file.getContentType())
            .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 || response.statusCode() == 201) {
            // Return the public URL
            return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + fileName;
        } else {
            throw new RuntimeException("Failed to upload image to Supabase: " + response.body());
        }
    }

    // Helper to generate a thumbnail MultipartFile from an image MultipartFile
    private MultipartFile generateThumbnail(MultipartFile original, int width, int height) throws Exception {
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(original.getBytes()));
        BufferedImage thumbImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        thumbImg.getGraphics().drawImage(img, 0, 0, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbImg, "jpg", baos);
        byte[] thumbBytes = baos.toByteArray();
        return new ByteArrayMultipartFile(thumbBytes, "file", "thumb.jpg", "image/jpeg");
    }

    // Custom MultipartFile implementation for in-memory byte[]
    public static class ByteArrayMultipartFile implements MultipartFile {
        private final byte[] bytes;
        private final String name;
        private final String originalFilename;
        private final String contentType;

        public ByteArrayMultipartFile(byte[] bytes, String name, String originalFilename, String contentType) {
            this.bytes = bytes;
            this.name = name;
            this.originalFilename = originalFilename;
            this.contentType = contentType;
        }

        @Override public String getName() { return name; }
        @Override public String getOriginalFilename() { return originalFilename; }
        @Override public String getContentType() { return contentType; }
        @Override public boolean isEmpty() { return bytes == null || bytes.length == 0; }
        @Override public long getSize() { return bytes.length; }
        @Override public byte[] getBytes() { return bytes; }
        @Override public InputStream getInputStream() { return new ByteArrayInputStream(bytes); }
        @Override public void transferTo(File dest) throws IOException, IllegalStateException {
            try (FileOutputStream fos = new FileOutputStream(dest)) {
                fos.write(bytes);
            }
        }
    }

    @GetMapping("/getAllPets")
    public List<PetEntity> getAllPets(){
        return pserv.getAllPets();
    }

    @GetMapping("/getPet/{id}")
    public PetEntity getPetById(@PathVariable int id) {
        return pserv.getPetById(id);
    }

    @PutMapping("/putPetDetails")
    public PetEntity putPetDetails(@RequestParam int pid, @RequestBody PetEntity newPetDetails){
        System.out.println("[DEBUG] Incoming PUT /putPetDetails for pid=" + pid + ", body: " + newPetDetails);
        PetEntity updated = pserv.putPetDetails(pid, newPetDetails);
        if (updated != null) {
            // Update weight, color, height if provided
            if (newPetDetails.getWeight() != null) updated.setWeight(newPetDetails.getWeight());
            if (newPetDetails.getColor() != null) updated.setColor(newPetDetails.getColor());
            if (newPetDetails.getHeight() != null) updated.setHeight(newPetDetails.getHeight());
        }
        System.out.println("[DEBUG] Outgoing response for PUT /putPetDetails: " + updated);
        return updated;
    }

    @DeleteMapping("/deletePetDetails/{pid}")
    public String deletePet(@PathVariable int pid){
        return pserv.deletePet(pid);
    }

    @GetMapping("/byUserName/{userName}")
    public List<PetEntity> getPetsByUserName(@PathVariable String userName) {
        return pserv.getPetsByUserName(userName);
    }

    @GetMapping("/searchByName/{name}")
    public List<PetEntity> searchPetsByName(@PathVariable String name) {
        return pserv.searchPetsByName(name);
    }
}