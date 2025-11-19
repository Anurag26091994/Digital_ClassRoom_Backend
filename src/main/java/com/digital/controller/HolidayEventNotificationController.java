package com.digital.controller;

import com.digital.entity.HolidayEventNotification;
import com.digital.servicei.HolidayEventNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@AllArgsConstructor
@RequestMapping(value = "/holiday-event-notification")
public class HolidayEventNotificationController {

    private final HolidayEventNotificationService holidayEventNotificationService;

    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<List<HolidayEventNotification>> getNotifications(@PathVariable Long userId){
        return new ResponseEntity<List<HolidayEventNotification>>
                (holidayEventNotificationService.findAllByUserId(userId), HttpStatus.OK);
    }

    @PutMapping(value = "/seen/{holidayEventNotificationId}")
    public ResponseEntity<Void> markAsSeen(@PathVariable Long holidayEventNotificationId) {

        return new ResponseEntity<Void>(holidayEventNotificationService.markAsSeen(holidayEventNotificationId),
                HttpStatus.OK);
    }
}
