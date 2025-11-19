package com.digital.serviceimpl;

import com.digital.entity.HolidayEventNotification;
import com.digital.repository.HolidayEventNotificationRepository;
import com.digital.servicei.HolidayEventNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class HolidayEventNotificationServiceImpl implements HolidayEventNotificationService {

    private final HolidayEventNotificationRepository holidayEventNotificationRepository;


    @Override
    public List<HolidayEventNotification> findAllByUserId(Long userId) {

        return holidayEventNotificationRepository.findAllByUserIdOrderByCreatedAtAsc(userId);
    }

    @Override
    public Void markAsSeen(Long holidayEventNotificationId) {

        holidayEventNotificationRepository.findById(holidayEventNotificationId)
                                          .ifPresent(notification -> {
                                              notification.setSeen(true);
                                              holidayEventNotificationRepository.save(notification);
                                          });
        return null;
    }
}
