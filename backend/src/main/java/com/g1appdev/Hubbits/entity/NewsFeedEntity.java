package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "news_feed")
public class NewsFeedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long articleID;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date publishedDate;

    private String author;

    @Column(name = "imageUrl", length = 255)
    private String imageUrl;

    @Column(name = "link", length = 255)
    private String link;

    // Getters and Setters...

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    // Default constructor
    public NewsFeedEntity() {}

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
