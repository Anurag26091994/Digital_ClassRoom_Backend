package com.digital.repository;

import com.digital.entity.PTMNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PTMNotificationRepository extends JpaRepository<PTMNotification, Long> {
    List<PTMNotification> findByParent_ParentIdOrderByCreatedAtDesc(Long parentId);
}
