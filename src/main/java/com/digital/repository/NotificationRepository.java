package com.digital.repository;




import com.digital.entity.Notification;
import com.digital.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByTeacher(Teacher teacher);
    List<Notification> findByParent_ParentId(Long parentId);
    // Fetch notifications for a parent, ordered by newest first
    List<Notification> findByParent_ParentIdOrderByCreatedAtDesc(Long parentId);

    // Optional: fetch only unseen notifications for a parent

    List<Notification> findByParent_ParentIdAndSeenFalseOrderByCreatedAtDesc(Long parentId);
}

