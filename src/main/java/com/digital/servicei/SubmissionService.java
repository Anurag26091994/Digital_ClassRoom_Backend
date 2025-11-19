package com.digital.servicei;

import com.digital.dto.SubmissionRequest;
import com.digital.dto.SubmissionResponse;

import java.util.List;

public interface SubmissionService {
    void submitExam(Long examId, Long studentId, SubmissionRequest request);
    List<SubmissionResponse> getSubmissionsForExam(Long examId, Long teacherId);
    SubmissionResponse getSubmission(Long examId, Long studentId);


}
