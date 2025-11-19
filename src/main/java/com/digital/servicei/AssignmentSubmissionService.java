package com.digital.servicei;

import com.digital.dto.SubmissionResponse;
import com.digital.exception.FileUploadException;
import com.digital.exception.ResourceNotFoundException;
import com.digital.exception.SubmissionAlreadyExistsException;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;

public interface AssignmentSubmissionService {
    SubmissionResponse submitAssignment(Long studentId, Long assignmentId, MultipartFile file)
            throws ResourceNotFoundException, SubmissionAlreadyExistsException, FileUploadException, Exception;

    SubmissionResponse getSubmission(Long submissionId) throws ResourceNotFoundException, Exception;

    SubmissionResponse updateSubmissionFile(Long submissionId, MultipartFile file)
            throws ResourceNotFoundException, FileUploadException, Exception;

    SubmissionResponse updateFeedbackAndMarks(Long submissionId, String feedback, Double marks)
            throws ResourceNotFoundException, Exception;

//    SubmissionResponse updateFeedbackAndMarks(Long submissionId, String feedback, Double marks)
//            throws ResourceNotFoundException, Exception;


    @Transactional
    SubmissionResponse updateSubmission(Long submissionId, MultipartFile file) throws SQLException;

    void deleteSubmission(Long submissionId) throws ResourceNotFoundException;


}
