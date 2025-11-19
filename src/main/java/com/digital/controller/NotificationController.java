package com.digital.controller;

import com.digital.dto.NotificationDto;
import com.digital.enums.EventType;
import com.digital.servicei.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // ✅ TEACHER: Get their own notifications
    @PreAuthorize("hasRole('TEACHER')")
    @GetMapping("/id")
    public ResponseEntity<?> getMyNotifications() {
        try {
            List<NotificationDto> notifications = notificationService.getMyNotifications();
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to retrieve notifications: " + e.getMessage()));
        }
    }

    // ✅ ADMIN: Send a notification to a teacher
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/send/{teacherId}")
    public ResponseEntity<?> sendNotification(@PathVariable Long teacherId,
                                              @RequestBody String message) {
        try {
            notificationService.sendNotification(teacherId, EventType.RESERVATION_AVAILABLE, message);
            return ResponseEntity.ok(Map.of("message", "Notification sent successfully."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to send notification: " + e.getMessage()));
        }
    }

    // ✅ TEACHER: Mark a notification as read
    @PreAuthorize("hasRole('TEACHER')")
    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(Map.of("message", "Notification marked as read."));
        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("error", "Failed to mark notification as read: " + e.getMessage()));
        }
    }
}
