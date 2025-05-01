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