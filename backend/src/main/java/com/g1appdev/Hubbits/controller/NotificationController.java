package com.g1appdev.Hubbits.controller;

import com.g1appdev.Hubbits.entity.NotificationEntity;
import com.g1appdev.Hubbits.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Get all notifications
    @GetMapping
    public ResponseEntity<List<NotificationEntity>> getAllNotifications() {
        List<NotificationEntity> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    // Get notification by ID
    @GetMapping("/{id}")
    public ResponseEntity<Optional<NotificationEntity>> getNotificationById(@PathVariable Long id) {
        Optional<NotificationEntity> notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    // Create new notification
    @PostMapping
    public ResponseEntity<NotificationEntity> createNotification(@RequestBody NotificationEntity notification, UriComponentsBuilder ucb) {
        NotificationEntity createdNotification = notificationService.createNotification(notification);
        URI location = ucb.path("/api/notifications/{id}")
                .buildAndExpand(createdNotification.getNotificationId())
                .toUri();
        return ResponseEntity.created(location).body(createdNotification);
    }

    // Update notification by ID
    @PutMapping("/{id}")
    public ResponseEntity<NotificationEntity> updateNotification(@PathVariable Long id, @RequestBody NotificationEntity notification) {
        NotificationEntity updatedNotification = notificationService.updateNotification(id, notification);
        return ResponseEntity.ok(updatedNotification);

    }

    // Delete notification by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        boolean deleted = notificationService.deleteNotification(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}