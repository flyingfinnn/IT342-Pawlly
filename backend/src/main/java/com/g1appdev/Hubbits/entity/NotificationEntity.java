package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notification_id;

    private Timestamp notification_date_and_time;
    private String notification_description;
    private String notification_title;
    private Long pet_id;
    private String pet_name;

    public NotificationEntity() {}

    // Getters and setters
    public Long getNotification_id() { return notification_id; }
    public void setNotification_id(Long notification_id) { this.notification_id = notification_id; }

    public Timestamp getNotification_date_and_time() { return notification_date_and_time; }
    public void setNotification_date_and_time(Timestamp notification_date_and_time) { this.notification_date_and_time = notification_date_and_time; }

    public String getNotification_description() { return notification_description; }
    public void setNotification_description(String notification_description) { this.notification_description = notification_description; }

    public String getNotification_title() { return notification_title; }
    public void setNotification_title(String notification_title) { this.notification_title = notification_title; }

    public Long getPet_id() { return pet_id; }
    public void setPet_id(Long pet_id) { this.pet_id = pet_id; }

    public String getPet_name() { return pet_name; }
    public void setPet_name(String pet_name) { this.pet_name = pet_name; }
} 