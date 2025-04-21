package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.NewsFeedEntity;
import com.g1appdev.Hubbits.service.NewsFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/newsfeed")
@CrossOrigin(origins = "http://localhost:3000")
public class NewsFeedController {

    @Autowired
    private NewsFeedService newsFeedService;

    @GetMapping
    public ResponseEntity<List<NewsFeedEntity>> getAllArticles() {
        List<NewsFeedEntity> articles = newsFeedService.getAllArticles();
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<NewsFeedEntity> getArticleById(@PathVariable Long id) {
        Optional<NewsFeedEntity> article = newsFeedService.getArticleById(id);
        return article.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> createArticle(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("author") String author,
            @RequestParam(value = "link", required = false) String link,
            @RequestParam(value = "imagefile", required = false) MultipartFile imageFile) {

        NewsFeedEntity article = new NewsFeedEntity();
        article.setTitle(title);
        article.setContent(content);
        article.setAuthor(author);
        article.setLink(link);

        try {
            newsFeedService.createArticle(article, imageFile);
            return ResponseEntity.ok("Article created successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to create article: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateArticle(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("author") String author,
            @RequestParam(value = "link", required = false) String link,
            @RequestParam(value = "imagefile", required = false) MultipartFile imageFile) {

        NewsFeedEntity updatedArticle = new NewsFeedEntity();
        updatedArticle.setTitle(title);
        updatedArticle.setContent(content);
        updatedArticle.setAuthor(author);
        updatedArticle.setLink(link);

        try {
            newsFeedService.updateArticle(id, updatedArticle, imageFile);
            return ResponseEntity.ok("Article updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to update article: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteArticle(@PathVariable Long id) {
        try {
            boolean deleted = newsFeedService.deleteArticle(id);
            if (deleted) {
                return ResponseEntity.ok("Article deleted successfully.");
            } else {
                return ResponseEntity.notFound().build();  // If article with ID does not exist
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete article: " + e.getMessage());
        }
    }

}
