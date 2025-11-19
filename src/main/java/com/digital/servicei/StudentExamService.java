package com.digital.servicei;

import com.digital.dto.*;

import java.util.List;

public interface StudentExamService {
    List<UpcomingExamResponse> getExamsForStudent(Long studentId);
    void submitExam(Long examId, Long studentId, SubmissionRequest request);
    List<SubmissionResponse> getResults(Long studentId);
    ReportCardResponse getReportCard(Long studentId, String term);

    ReportCardViewDto getReportCardView(Long studentRegId, String term);

}
