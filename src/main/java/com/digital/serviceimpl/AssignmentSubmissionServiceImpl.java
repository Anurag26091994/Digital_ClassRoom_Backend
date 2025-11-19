package com.digital.serviceimpl;

import com.digital.dto.SubmissionResponse;
import com.digital.entity.Assignment;
import com.digital.entity.AssignmentSubmission;
import com.digital.enums.SubmissionStatus;
import com.digital.exception.ResourceNotFoundException;
import com.digital.exception.SubmissionAlreadyExistsException;
import com.digital.exception.FileUploadException;
import com.digital.repository.AssignmentRepository;
import com.digital.repository.AssignmentSubmissionRepository;
import com.digital.servicei.AssignmentSubmissionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
public class AssignmentSubmissionServiceImpl implements AssignmentSubmissionService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private AssignmentSubmissionRepository submissionRepository;

    // Convert MultipartFile to Blob
    private Blob convertMultipartToBlob(MultipartFile file) throws FileUploadException {
        try {
            byte[] bytes = file.getBytes();
            return new SerialBlob(bytes);
        } catch (Exception e) {
            throw new FileUploadException("Could not convert file to blob", e);
        }
    }

    @Override
    @Transactional
    public SubmissionResponse submitAssignment(Long studentId, Long assignmentId, MultipartFile file)
            throws ResourceNotFoundException, SubmissionAlreadyExistsException, FileUploadException, SQLException {

        // Check if assignment exists
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));

        // Check if student has already submitted
        boolean exists = submissionRepository.existsByAssignment_AssignmentIdAndStudentId(assignmentId, studentId);
        if (exists) {
            throw new SubmissionAlreadyExistsException("Submission already exists for assignment " + assignmentId + " and student " + studentId);
        }

        // Convert file
        Blob fileBlob = convertMultipartToBlob(file);

        // Build submission
        AssignmentSubmission submission = AssignmentSubmission.builder()
                .assignment(assignment)
                .studentId(studentId)
                .fileUrl(fileBlob)
                .submittedAt(LocalDateTime.now())
                .status(SubmissionStatus.SUBMITTED)
                .marks(0.0)
                .feedback("Pending review")
                .build();

        AssignmentSubmission saved = submissionRepository.save(submission);

        // Map to response
        SubmissionStatus status = saved.getStatus() != null ? saved.getStatus() : SubmissionStatus.PENDING;

        return SubmissionResponse.builder()
                .submissionId(saved.getSubmissionId())
                .assignmentId(saved.getAssignment().getAssignmentId())
                .studentId(saved.getStudentId())
                .fileUrl(saved.getFileUrl().getBytes(1, (int) saved.getFileUrl().length()))
                .submittedAt(saved.getSubmittedAt())
                .assignmentStatus(status)
                .marks(saved.getMarks())
                .feedback(saved.getFeedback())
                .build();
    }

    @Override
    public SubmissionResponse getSubmission(Long submissionId) throws SQLException {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        SubmissionStatus status = submission.getStatus() != null ? submission.getStatus() : SubmissionStatus.PENDING;

        return SubmissionResponse.builder()
                .submissionId(submission.getSubmissionId())
                .assignmentId(submission.getAssignment().getAssignmentId())
                .studentId(submission.getStudentId())
                .fileUrl(submission.getFileUrl().getBytes(1, (int) submission.getFileUrl().length()))
                .submittedAt(submission.getSubmittedAt())
                .assignmentStatus(status)
                .marks(submission.getMarks())
                .feedback(submission.getFeedback())
                .build();
    }

    @Override
    public SubmissionResponse updateSubmissionFile(Long submissionId, MultipartFile file)
            throws ResourceNotFoundException, FileUploadException, Exception {

        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        if (file != null && !file.isEmpty()) {
            Blob fileBlob = convertMultipartToBlob(file);
            submission.setFileUrl(fileBlob);
            submission.setSubmittedAt(LocalDateTime.now());
        }

        AssignmentSubmission updated = submissionRepository.save(submission);

        SubmissionStatus status = updated.getStatus() != null ? updated.getStatus() : SubmissionStatus.PENDING;

        return SubmissionResponse.builder()
                .submissionId(updated.getSubmissionId())
                .assignmentId(updated.getAssignment().getAssignmentId())
                .studentId(updated.getStudentId())
                .fileUrl(updated.getFileUrl().getBytes(1, (int) updated.getFileUrl().length()))
                .submittedAt(updated.getSubmittedAt())
                .assignmentStatus(status)
                .marks(updated.getMarks())
                .feedback(updated.getFeedback())
                .build();
    }

    @Override
    @Transactional
    public SubmissionResponse updateFeedbackAndMarks(Long submissionId, String feedback, Double marks)
            throws ResourceNotFoundException, Exception {

        AssignmentSubmission existing = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        existing.setFeedback(feedback);
        existing.setMarks(marks);
        existing.setStatus(SubmissionStatus.SUBMITTED);

        AssignmentSubmission updated = submissionRepository.save(existing);

        SubmissionStatus status = updated.getStatus() != null ? updated.getStatus() : SubmissionStatus.PENDING;

        return SubmissionResponse.builder()
                .submissionId(updated.getSubmissionId())
                .assignmentId(updated.getAssignment().getAssignmentId())
                .studentId(updated.getStudentId())
                .fileUrl(updated.getFileUrl().getBytes(1, (int) updated.getFileUrl().length()))
                .submittedAt(updated.getSubmittedAt())
                .assignmentStatus(status)
                .marks(updated.getMarks())
                .feedback(updated.getFeedback())
                .build();
    }

    @Override
    @Transactional
    public SubmissionResponse updateSubmission(Long submissionId, MultipartFile file) throws SQLException {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));

        if (file != null && !file.isEmpty()) {
            Blob fileBlob = convertMultipartToBlob(file);
            submission.setFileUrl(fileBlob);
            submission.setSubmittedAt(LocalDateTime.now());
        }

        submission.setStatus(SubmissionStatus.SUBMITTED);
        submission.setMarks(0.0);
        submission.setFeedback("Pending review");

        AssignmentSubmission updated = submissionRepository.save(submission);

        SubmissionStatus status = updated.getStatus() != null ? updated.getStatus() : SubmissionStatus.PENDING;

        return SubmissionResponse.builder()
                .submissionId(updated.getSubmissionId())
                .assignmentId(updated.getAssignment().getAssignmentId())
                .studentId(updated.getStudentId())
                .fileUrl(updated.getFileUrl().getBytes(1, (int) updated.getFileUrl().length()))
                .submittedAt(updated.getSubmittedAt())
                .assignmentStatus(status)  // DTO field
                .marks(updated.getMarks())
                .feedback(updated.getFeedback())
                .build();
    }


    @Override
    @Transactional
    public void deleteSubmission(Long submissionId) throws ResourceNotFoundException {
        AssignmentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found with ID: " + submissionId));
        submissionRepository.delete(submission);
    }
}
