package com.g1appdev.Hubbits.service;

import com.g1appdev.Hubbits.entity.NotificationEntity;
import com.g1appdev.Hubbits.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotificationService {
    @Autowired
    private NotificationRepository notificationRepository;

    public List<NotificationEntity> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public Optional<NotificationEntity> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    public NotificationEntity createNotification(NotificationEntity notification) {
        return notificationRepository.save(notification);
    }

    public NotificationEntity updateNotification(Long id, NotificationEntity notification) {
        return notificationRepository.findById(id)
                .map(existing -> {
                    existing.setNotification_date_and_time(notification.getNotification_date_and_time());
                    existing.setNotification_description(notification.getNotification_description());
                    existing.setNotification_title(notification.getNotification_title());
                    existing.setPet_id(notification.getPet_id());
                    existing.setPet_name(notification.getPet_name());
                    return notificationRepository.save(existing);
                })
                .orElse(null);
    }

    public boolean deleteNotification(Long id) {
        if (notificationRepository.existsById(id)) {
            notificationRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 