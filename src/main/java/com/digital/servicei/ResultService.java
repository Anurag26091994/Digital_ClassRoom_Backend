package com.digital.servicei;

import com.digital.dto.ResultResponse;

import java.util.List;

public interface ResultService {
    ResultResponse getResult(Long examId, Long studentId);
    List<ResultResponse> getResultsByExam(Long examId);
    ResultResponse publishResult(Long examId, Long studentId, Long adminId);
    List<ResultResponse> publishAllResults(Long examId, Long adminId);
}
