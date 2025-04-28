package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.NewsFeedEntity;
import com.g1appdev.Hubbits.service.NewsFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/newsfeed")
@CrossOrigin(origins = "http://localhost:3000") // Allow requests from your frontend URL
public class NewsFeedController {

    @Autowired
    private NewsFeedService newsFeedService;

    // Get all articles
    @GetMapping
    public List<NewsFeedEntity> getAllArticles() {
        return newsFeedService.getAllArticles();
    }

    // Get article by ID
    @GetMapping("/{id}")
    public ResponseEntity<NewsFeedEntity> getArticleById(@PathVariable Long id) {
        Optional<NewsFeedEntity> article = newsFeedService.getArticleById(id);
        return article.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Create a single article
    @PostMapping
    public NewsFeedEntity createArticle(@RequestBody NewsFeedEntity article) {
        return newsFeedService.createArticle(article);
    }

    // Update article by ID
    @PutMapping("/{id}")
    public ResponseEntity<NewsFeedEntity> updateArticle(@PathVariable Long id, @RequestBody NewsFeedEntity article) {
        NewsFeedEntity updatedArticle = newsFeedService.updateArticle(id, article);
        if (updatedArticle != null) {
            return ResponseEntity.ok(updatedArticle);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete article by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        boolean deleted = newsFeedService.deleteArticle(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
