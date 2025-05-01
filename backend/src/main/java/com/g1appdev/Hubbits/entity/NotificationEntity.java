package com.g1appdev.Hubbits.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    @Column(name = "notification_title", nullable = false)
    private String notificationTitle;

    @Column(name = "notification_description", nullable = false)
    private String notificationDescription;

    @Column(name = "notification_date_and_time", nullable = false)
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

// Service Class: NotificationService.java
package com.g1appdev.Hubbits.service;

        import com.g1appdev.Hubbits.entity.NotificationEntity;
        import com.g1appdev.Hubbits.repository.NotificationRepository;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.stereotype.Service;
        import org.springframework.http.HttpStatus;
        import org.springframework.web.server.ResponseStatusException;

        import java.util.List;
        import java.util.Optional;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    // Get all notifications
    public List<NotificationEntity> getAllNotifications() {
        return notificationRepository.findAll();
    }

    // Get notification by ID
    public Optional<NotificationEntity> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    // Create new notification
    public NotificationEntity createNotification(NotificationEntity notification) {
        return notificationRepository.save(notification);
    }

    // Update notification by ID
    public NotificationEntity updateNotification(Long id, NotificationEntity notification) {
        Optional<NotificationEntity> existingNotification = notificationRepository.findById(id);
        if (existingNotification.isPresent()) {
            NotificationEntity existing = existingNotification.get();
            existing.setNotificationTitle(notification.getNotificationTitle());
            existing.setNotificationDescription(notification.getNotificationDescription());
            existing.setNotificationDateAndTime(notification.getNotificationDateAndTime());
            return notificationRepository.save(existing);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Notification not found with id: " + id);
        }
    }

    // Delete notification by ID
    public boolean deleteNotification(Long id) {
        Optional<NotificationEntity> notification = notificationRepository.findById(id);
        if (notification.isPresent()) {
            notificationRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}