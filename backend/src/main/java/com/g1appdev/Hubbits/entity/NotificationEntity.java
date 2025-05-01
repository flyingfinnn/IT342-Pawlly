package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "notification_title", nullable = false)
    @NotBlank(message = "Notification title cannot be blank")
    private String notificationTitle;

    @Column(name = "notification_description", nullable = false)
    @NotBlank(message = "Notification description cannot be blank")
    private String notificationDescription;

    @Column(name = "notification_date_and_time", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime notificationDateAndTime;

    // Constructors
    public NotificationEntity() {
    }

    public NotificationEntity(String notificationTitle, String notificationDescription, LocalDateTime notificationDateAndTime) {
        this.notificationTitle = notificationTitle;
        this.notificationDescription = notificationDescription;
        this.notificationDateAndTime = notificationDateAndTime;
    }

    // Getters and Setters
    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationDescription() {
        return notificationDescription;
    }

    public void setNotificationDescription(String notificationDescription) {
        this.notificationDescription = notificationDescription;
    }

    public LocalDateTime getNotificationDateAndTime() {
        return notificationDateAndTime;
    }

    public void setNotificationDateAndTime(LocalDateTime notificationDateAndTime) {
        this.notificationDateAndTime = notificationDateAndTime;
    }
}