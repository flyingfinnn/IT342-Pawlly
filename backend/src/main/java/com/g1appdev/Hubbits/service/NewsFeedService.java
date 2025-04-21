package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.NewsFeedEntity;
import com.g1appdev.Hubbits.repository.NewsFeedRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

@Service
public class NewsFeedService {

    @Autowired
    private NewsFeedRepository repository;

    private static final String UPLOAD_DIR = "src/main/resources/static/newsfeed-images";

    public String uploadImage(MultipartFile file) throws IOException {
        Path uploadPath = Path.of(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String newFilename = System.currentTimeMillis() + "-" + originalFilename;
        Path targetPath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        return "/newsfeed-images/" + newFilename;
    }

    public List<NewsFeedEntity> getAllArticles() {
        return repository.findAll();
    }

    // Get a specific article by ID
    public Optional<NewsFeedEntity> getArticleById(Long id) {
        return repository.findById(id);
    }

    public NewsFeedEntity createArticle(NewsFeedEntity article, MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String imageUrl = uploadImage(imageFile);
                article.setImageUrl(imageUrl);
            } catch (IOException e) {
                throw new RuntimeException("Error saving image file", e);
            }
        }
        // publishedDate is auto-set by the entity's @PrePersist method
        return repository.save(article);
    }

    public NewsFeedEntity updateArticle(Long id, NewsFeedEntity updatedArticle, MultipartFile imageFile) {
        return repository.findById(id).map(existingArticle -> {
            existingArticle.setTitle(updatedArticle.getTitle());
            existingArticle.setContent(updatedArticle.getContent());
            existingArticle.setAuthor(updatedArticle.getAuthor());
            existingArticle.setLink(updatedArticle.getLink());
            
            // Preserve existing publishedDate
            if (updatedArticle.getPublishedDate() != null) {
                existingArticle.setPublishedDate(updatedArticle.getPublishedDate());
            }

            if (imageFile != null && !imageFile.isEmpty()) {
                try {
                    String imageUrl = uploadImage(imageFile);
                    existingArticle.setImageUrl(imageUrl);
                } catch (IOException e) {
                    throw new RuntimeException("Error updating image file", e);
                }
            }

            return repository.save(existingArticle);
        }).orElseThrow(() -> new IllegalArgumentException("Article not found"));
    }

    public boolean deleteArticle(Long id) {
        Optional<NewsFeedEntity> article = repository.findById(id);
        if (article.isPresent()) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
    
}
