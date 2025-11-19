package com.digital.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Blob;
import java.util.List;

@Data
public class SubmissionRequest {
    @NotNull
    private Long assignmentId;

    @NotNull
    private Blob fileUrl;
    private List<AnswerDto> answers; // [{questionId, answer}]
}


