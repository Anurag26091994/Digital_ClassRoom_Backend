package com.digital.serviceimpl;

import com.digital.dto.AssignmentRequest;
import com.digital.dto.AssignmentResponse;
import com.digital.entity.Assignment;
import com.digital.exception.ResourceNotFoundException;
import com.digital.exception.FileUploadException;
import com.digital.repository.AssignmentRepository;
import com.digital.servicei.AssignmentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentServiceImpl implements AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;


    @Override
    public AssignmentResponse createAssignment(AssignmentRequest request, Long teacherId) throws Exception {
        Assignment assignment = Assignment.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dueDate(request.getDueDate())
                .createdAt(request.getCreatedAt() != null ? request.getCreatedAt() : LocalDateTime.now())
                .updatedAt(request.getUpdatedAt() != null ? request.getUpdatedAt() : LocalDateTime.now())
                .classId(request.getClassId())
                .sectionId(request.getSectionId())
                .subjectId(request.getSubjectId())
                .teacherId(teacherId)
                .fileUrl(request.getFileUrl())
                .build();

        Assignment saved = assignmentRepository.save(assignment);
        return mapToResponse(saved);
    }
    @Override
    public AssignmentResponse getAssignmentById(Long assignmentId) throws ResourceNotFoundException, SQLException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id " + assignmentId));
        return mapToResponse(assignment);
    }

    @Override
    public List<AssignmentResponse> getAllAssignments() {
        return assignmentRepository.findAll().stream()
                .map(a -> {
                    try {
                        return mapToResponse(a);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error mapping blob to byte[]", e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AssignmentResponse updateAssignment(Long assignmentId, AssignmentRequest request, Long teacherId)
            throws ResourceNotFoundException, Exception {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id " + assignmentId));

        if (!assignment.getTeacherId().equals(teacherId)) {
            throw new ResourceNotFoundException("Assignment not found for this teacher.");
        }

        if (request.getTitle() != null) assignment.setTitle(request.getTitle());
        if (request.getDescription() != null) assignment.setDescription(request.getDescription());
        if (request.getDueDate() != null) assignment.setDueDate(request.getDueDate());
        if (request.getFileUrl() != null) assignment.setFileUrl(request.getFileUrl());
        if (request.getClassId() != null) assignment.setClassId(request.getClassId());
        if (request.getSectionId() != null) assignment.setSectionId(request.getSectionId());
        if (request.getSubjectId() != null) assignment.setSubjectId(request.getSubjectId());

        assignment.setUpdatedAt(LocalDateTime.now());

        Assignment updated = assignmentRepository.save(assignment);
        return mapToResponse(updated);
    }

    @Override
    public void deleteAssignment(Long assignmentId) throws ResourceNotFoundException {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with id " + assignmentId));
        assignmentRepository.delete(assignment);
    }

    @Override
    public Blob convertMultipartToBlob(org.springframework.web.multipart.MultipartFile file) throws FileUploadException {
        try {
            byte[] bytes = file.getBytes();
            return new SerialBlob(bytes);
        } catch (Exception e) {
            throw new FileUploadException("Could not convert file to blob", e);
        }
    }

    @Override
    public List<AssignmentResponse> getAllAssignmentsByTeacherId(Long teacherId) {
        return assignmentRepository.findByTeacherId(teacherId).stream()
                .map(a -> {
                    try {
                        return mapToResponse(a);
                    } catch (SQLException e) {
                        throw new RuntimeException("Error mapping blob to byte[]", e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public AssignmentResponse getAssignmentByIdAndTeacherId(Long assignmentId, Long teacherId) throws ResourceNotFoundException {
        Assignment assignment = assignmentRepository.findByAssignmentIdAndTeacherId(assignmentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found for this teacher with id " + assignmentId));
        try {
            return mapToResponse(assignment);
        } catch (SQLException e) {
            throw new RuntimeException("Blob conversion error", e);
        }
    }

    @Override
    public void deleteAssignmentByIdAndTeacherId(Long assignmentId, Long teacherId) throws ResourceNotFoundException {
        Assignment assignment = assignmentRepository.findByAssignmentIdAndTeacherId(assignmentId, teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found for this teacher with id " + assignmentId));
        assignmentRepository.delete(assignment);
    }

    private AssignmentResponse mapToResponse(Assignment assignment) throws SQLException {
        AssignmentResponse resp = new AssignmentResponse();
        resp.setAssignmentId(assignment.getAssignmentId());
        resp.setTitle(assignment.getTitle());
        resp.setDescription(assignment.getDescription());
        resp.setDueDate(assignment.getDueDate());
        resp.setCreatedAt(assignment.getCreatedAt());
        resp.setUpdatedAt(assignment.getUpdatedAt());
        resp.setClassId(assignment.getClassId());
        resp.setSectionId(assignment.getSectionId());
        resp.setSubjectId(assignment.getSubjectId());
        resp.setTeacherId(assignment.getTeacherId());

        if (assignment.getFileUrl() != null) {
            Blob blob = assignment.getFileUrl();
            byte[] bytes = blob.getBytes(1, (int) blob.length());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            resp.setFileName(base64); // now fileName holds Base64 string of file content
        }
        return resp;
    }
}
