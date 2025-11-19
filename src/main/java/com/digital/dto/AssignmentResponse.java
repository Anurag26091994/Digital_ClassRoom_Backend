package com.digital.dto;
import com.digital.enums.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

//
//import com.digital.enums.SubmissionStatus;
//import jakarta.persistence.Lob;
//import lombok.*;
//
//import java.sql.Blob;
//import java.time.LocalDateTime;
//
//@Getter
//@Setter
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//public class AssignmentResponse {
//    private Long assignmentId;
//    private String title;
//    private String description;
//
//    @Lob
//    private byte[] fileName; // Optional: store file name
//
//    private LocalDateTime dueDate;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private Long classId;
//    private Long sectionId;
//    private Long subjectId;
//    private Long teacherId;
//    private Boolean submitted;
//    private LocalDateTime submittedAt;
//    private SubmissionStatus status;
//    private Double marks;
//    private String feedback;
//
//    public Long getAssignmentId() {
//        return assignmentId;
//    }
//
//    public void setAssignmentId(Long assignmentId) {
//        this.assignmentId = assignmentId;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//
//
//    public LocalDateTime getDueDate() {
//        return dueDate;
//    }
//
//    public void setDueDate(LocalDateTime dueDate) {
//        this.dueDate = dueDate;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }
//
//    public void setCreatedAt(LocalDateTime createdAt) {
//        this.createdAt = createdAt;
//    }
//
//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(LocalDateTime updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public Long getClassId() {
//        return classId;
//    }
//
//    public void setClassId(Long classId) {
//        this.classId = classId;
//    }
//
//    public Long getSectionId() {
//        return sectionId;
//    }
//
//    public void setSectionId(Long sectionId) {
//        this.sectionId = sectionId;
//    }
//
//    public Long getSubjectId() {
//        return subjectId;
//    }
//
//    public void setSubjectId(Long subjectId) {
//        this.subjectId = subjectId;
//    }
//
//    public Long getTeacherId() {
//        return teacherId;
//    }
//
//    public void setTeacherId(Long teacherId) {
//        this.teacherId = teacherId;
//    }
//}
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignmentResponse {
    private Long assignmentId;
    private String title;
    private String description;
    private String fileName;       // Use String for file name/path, not byte[]
    private LocalDateTime dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long classId;
    private Long sectionId;
    private Long subjectId;
    private Long teacherId;

    private Boolean submitted;
    private LocalDateTime submittedAt;
    private SubmissionStatus status;
    private Double marks;
    private String feedback;
}
