package com.digital.servicei;

import com.digital.entity.HolidayEventNotification;

import java.util.List;

public interface HolidayEventNotificationService {

    List<HolidayEventNotification> findAllByUserId(Long userId);

    Void markAsSeen(Long holidayEventNotificationId);
}
