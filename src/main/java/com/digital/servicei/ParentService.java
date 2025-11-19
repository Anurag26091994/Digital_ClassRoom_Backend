package com.digital.servicei;

import com.digital.dto.*;
import com.digital.entity.Attendance;
import com.digital.entity.Exam;
import com.digital.entity.Notification;
import com.digital.entity.Result;

import java.util.List;

public interface ParentService {
    ParentResponse createParent(ParentRequest req);
    ParentResponse updateParent(Long parentId, ParentRequest req);
    void deleteParent(Long parentId);
    List<ParentResponse> getAllParents();
    ParentResponse getParentById(Long parentId);

    // mapping
    void linkParentToStudent(Long parentId, Long studentId, String relationshipType);


    // byte[] generateReportCardPdf(Long parentId, Long studentId); // returns PDF bytes



//    Object getDashboard(Long parentId);
//    Object getAttendance(Long parentId, Long studentRegId);
//    Object getExams(Long parentId, Long studentRegId);
//    Object getResults(Long parentId, Long studentRegId);
//    Object getNotifications(Long parentId);
    //Object getPayments(Long parentId, String studentRegId);

    // Dashboard & child details
    ParentDashboardResponse getDashboard(Long parentId);
    List<Attendance> getAttendance(Long parentId, Long studentRegId);
    List<ParentDashboardResponse.UpcomingExamDto> getExams(Long parentId, Long studentRegId);
    //List<Result> getResults(Long parentId, Long studentRegId);
    //List<Notification> getNotifications(Long parentId);
    List<NotificationDto> getNotifications(Long parentId);
    List<ResultResponse> getResults(Long parentId, Long studentId);
    //List<AssignmentResponse> getAssignmentsForChild(Long parentId, Long studentId);
    List<AssignmentResponse> getAssignmentsForChild(Long parentId);
}

