package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.NewsFeedEntity;
import com.g1appdev.Hubbits.repository.NewsFeedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsFeedService {

    @Autowired
    private NewsFeedRepository newsFeedRepository;

    // Retrieve all news feed articles
    public List<NewsFeedEntity> getAllArticles() {
        return newsFeedRepository.findAll();
    }

    // Retrieve a news feed article by ID
    public Optional<NewsFeedEntity> getArticleById(Long articleID) {
        return newsFeedRepository.findById(articleID);
    }

    // Create a new news feed article
    public NewsFeedEntity createArticle(NewsFeedEntity newsFeed) {
        return newsFeedRepository.save(newsFeed);
    }

    // Update an existing news feed article by ID
    public NewsFeedEntity updateArticle(Long articleID, NewsFeedEntity updatedNewsFeed) {
        Optional<NewsFeedEntity> newsFeedOpt = newsFeedRepository.findById(articleID);
        if (newsFeedOpt.isPresent()) {
            NewsFeedEntity newsFeed = newsFeedOpt.get();
            newsFeed.setTitle(updatedNewsFeed.getTitle());
            newsFeed.setContent(updatedNewsFeed.getContent());
            newsFeed.setPublishedDate(updatedNewsFeed.getPublishedDate());
            newsFeed.setAuthor(updatedNewsFeed.getAuthor());
            return newsFeedRepository.save(newsFeed);
        }
        return null;
    }

    // Delete a news feed article by ID
    public boolean deleteArticle(Long articleID) {
        if (newsFeedRepository.existsById(articleID)) {
            newsFeedRepository.deleteById(articleID);
            return true;
        }
        return false;
    }
}
