package com.digital.listener;

import com.digital.entity.ExamNotification;
import com.digital.entity.HolidayEventNotification;
import com.digital.enums.NotificationType;
import com.digital.events.*;
import com.digital.repository.ExamNotificationRepository;
import com.digital.repository.HolidayEventNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final ExamNotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final HolidayEventNotificationRepository holidayEventNotificationRepository;

    @EventListener
    public void handleExamCreated(ExamCreatedEvent event) {
        String title = "New Exam Scheduled";
        String message = "A new exam has been scheduled. Exam ID: " + event.examId();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.EXAM, event.examId()));
        event.teacherIds().forEach(id -> sendNotification(id, title, message, NotificationType.EXAM, event.examId()));
    }

    @EventListener
    public void handleResultPublished(ResultPublishedEvent event) {
        String title = "Result Published";
        String message = "Your exam results are now available. Result ID: " + event.resultId();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.RESULT, event.resultId()));
    }

    @EventListener
    public void handleReportCardGenerated(ReportCardGeneratedEvent event) {
        String title = "Report Card Available";
        String message = "Your report card is now available. ReportCard ID: " + event.reportCardId();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.REPORT_CARD, event.reportCardId()));
    }

    @EventListener
    public void handleEmergencyHolidayEvent(EmergencyHolidayEvent event){
        String title = "Emergency Holiday";
        String message = "School has declared emergency holiday, id: " + event.holidayId() +
                " on date " + event.date();

       event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.EMERGENCY_HOLIDAY, event.holidayId()));
       event.teacherIds().forEach(id -> sendNotification(id, title, message, NotificationType.EMERGENCY_HOLIDAY, event.holidayId()));
    }

    @EventListener
    public void handleRescheduledHolidayEvent(RescheduledHolidayEvent event){
        String title = "Holiday Rescheduled";
        String message = "School has rescheduled the " + event.holidayName() + " holiday, id: " + event.holidayId() +
                " from date " + event.fromDate() + " to " + event.rescheduledDate();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.RESCHEDULED_HOLIDAY, event.holidayId()));
        event.teacherIds().forEach(id -> sendNotification(id, title, message, NotificationType.RESCHEDULED_HOLIDAY, event.holidayId()));
    }

    @EventListener
    public void handleRescheduledEvent(RescheduledEvent event){
        String title = "Event Rescheduled";
        String message = "School has rescheduled the event " + event.eventName() + ", id: " + event.eventId()
                + " from date " + event.fromDate() + " to date " + event.toDate();

        event.studentIds().forEach(id -> sendNotification(id, title, message, NotificationType.RESCHEDULED_EVENT, event.eventId()));
        event.teacherIds().forEach(id -> sendNotification(id, title, message, NotificationType.RESCHEDULED_EVENT, event.eventId()));
    }

    @EventListener
    public void handleLeaveApprovalEvent(LeaveApprovalEvent event){
        String title = "Leave Approval";
        String message = "Your leave request with id " + event.leaveId() + " has : " + event.status()
                + " from date " + event.fromDate() + " to date " + event.toDate();

        sendNotification(event.userId(), title, message, NotificationType.LEAVE_APPROVAL, event.leaveId());
    }


    @EventListener
    public void handleTeacherOnLeaveEvent(TeacherOnLeaveEvent event){
        String title = "Teacher on leave";
        String message = "Teacher with id " + event.teacherId() + " is on leave id: " +
                event.leaveId() + " from date " + event.fromDate() + " to date " + event.toDate() +
                "class which are affected are " + event.schoolClassIds().toString();

        sendNotification(event.userId(), title, message, NotificationType.LEAVE_APPROVAL, event.leaveId());
    }

    private void sendNotification(Long userId, String title, String message, NotificationType type, Long refId) {

        if((type.equals(NotificationType.EMERGENCY_HOLIDAY)) | (type.equals(NotificationType.RESCHEDULED_EVENT))
        | (type.equals(NotificationType.RESCHEDULED_HOLIDAY)) | type.equals(NotificationType.LEAVE_APPROVAL)){

            HolidayEventNotification notification = HolidayEventNotification.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .referenceId(refId)
                    .build();

            holidayEventNotificationRepository.save(notification);

            messagingTemplate.convertAndSend("/topic/user/" + userId, notification);
        }
        if(type.equals(NotificationType.EXAM)) {

            ExamNotification notification = ExamNotification.builder()
                    .userId(userId)
                    .title(title)
                    .message(message)
                    .type(type)
                    .referenceId(refId)
                    .build();

            notificationRepository.save(notification);

            messagingTemplate.convertAndSend("/topic/user/" + userId, notification);
        }

    }
}