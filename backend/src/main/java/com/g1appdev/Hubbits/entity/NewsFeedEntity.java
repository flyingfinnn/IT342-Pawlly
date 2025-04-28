package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "news_feed")
public class NewsFeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleID;  // articleID as the primary key
    
    private String title;  // title of the news article
    
    @Column(columnDefinition = "TEXT")
    private String content;  // content of the article (using TEXT for larger text data)
    
    @Temporal(TemporalType.TIMESTAMP)  // Ensures both date and time are saved
    private Date publishedDate;  // date the article was published
    
    private String author;  // author of the article

    // Default constructor
    public NewsFeedEntity() {
    }

    // Getter and Setter methods
    public Long getArticleID() {
        return articleID;
    }

    public void setArticleID(Long articleID) {
        this.articleID = articleID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    // Automatically set the current date and time when an article is created
    @PrePersist
    public void setDefaultPublishedDate() {
        if (this.publishedDate == null) {
            this.publishedDate = new Date();  // Set the current date and time
        }
    }
}
