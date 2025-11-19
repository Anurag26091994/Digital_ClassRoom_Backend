package com.digital.repository;


import com.digital.entity.HolidayEventNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayEventNotificationRepository extends JpaRepository<HolidayEventNotification, Long> {

    List<HolidayEventNotification> findAllByUserIdOrderByCreatedAtAsc(Long userId);
}
