package com.digital.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ParentDashboardResponse {
    private Long parentId;
    private String parentName;
    private Long userId;
    private List<ChildSummary> children;
    private List<NotificationDto> notifications;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChildSummary {
        private Long studentRegId;
        private String studentName;
        private Double attendancePercent;
        private List<UpcomingExamDto> upcomingExams;
        private List<AssignmentDto> assignments;
        private FeeSummary feeSummary;
        private List<ParentDashboardResponse.PTMDto> ptms;

    }

    @Data
    @Builder
    public static class UpcomingExamDto {
        private Long examId;
        private String title;
        private String subject;
        private String examDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssignmentDto {
        private Long assignmentId;
        private String title;
        private String dueDate;
        private Long subject_id;
        private String subjectName;
        private boolean submitted;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FeeSummary {
        private Double dueAmount;
        private String dueDate;
        private Double totalFee;
        private Double paidAmount;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class NotificationDto {
        private Long id;
        private String title;
        private String body;
        private String message;
        private String type;
        private String createdAt;
    }
//    @Data
//    @Builder
//    public static class ExamDto {
//        private Long examId;
//        private String title;
//        private String subject;
//        private String examDate;
//    }
@Data
@Builder
public static class PendingAssignmentDto {
    private Long assignmentId;
    private String title;
    private String dueDate;
}
    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PTMDto {
        private Long ptmId;
        private String title;
        private String meetingDateTime;
        private String joinLink;
        private String status;
        private String venue;
    }

}
