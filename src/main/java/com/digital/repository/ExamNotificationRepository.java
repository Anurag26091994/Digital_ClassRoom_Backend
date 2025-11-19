package com.digital.repository;

import com.digital.entity.ExamNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamNotificationRepository extends JpaRepository<ExamNotification, Long> {

    List<ExamNotification> findByUserIdOrderByCreatedAtDesc(Long userId);
}