package com.digital.servicei;

import com.digital.dto.AssignmentRequest;
import com.digital.dto.AssignmentResponse;
import com.digital.exception.ResourceNotFoundException;
import com.digital.exception.FileUploadException;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

public interface AssignmentService {

    AssignmentResponse createAssignment(AssignmentRequest request, Long teacherId)
            throws Exception;

    AssignmentResponse getAssignmentById(Long assignmentId)
            throws ResourceNotFoundException, SQLException;

    List<AssignmentResponse> getAllAssignments()
            throws SQLException;

    AssignmentResponse updateAssignment(Long assignmentId, AssignmentRequest request, Long teacherId)
            throws ResourceNotFoundException, Exception;

    void deleteAssignment(Long assignmentId)
            throws ResourceNotFoundException;

    Blob convertMultipartToBlob(MultipartFile file)
            throws FileUploadException, Exception;

    List<AssignmentResponse> getAllAssignmentsByTeacherId(Long teacherId);

    AssignmentResponse getAssignmentByIdAndTeacherId(Long assignmentId, Long teacherId)
            throws ResourceNotFoundException;

    void deleteAssignmentByIdAndTeacherId(Long assignmentId, Long teacherId)
            throws ResourceNotFoundException;
}
