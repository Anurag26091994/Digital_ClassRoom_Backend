package com.digital.controller;

import com.digital.entity.ExamNotification;
import com.digital.repository.ExamNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExamNotificationController {
  private final ExamNotificationRepository examNotificationRepository;

    @GetMapping
    public List<ExamNotification> getNotifications(@RequestParam Long userId) {
        return examNotificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @PatchMapping("/{id}/seen")
    public void markAsSeen(@PathVariable Long id) {
        examNotificationRepository.findById(id).ifPresent(n -> {
            n.setSeen(true);
            examNotificationRepository.save(n);
        });
    }
}

