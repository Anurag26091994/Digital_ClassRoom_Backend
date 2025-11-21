package com.digital.controller;

import com.digital.dto.AssignmentResponse;
import com.digital.dto.NotificationDto;
import com.digital.dto.ParentDashboardResponse;
import com.digital.dto.ResultResponse;
import com.digital.entity.*;
import com.digital.exception.ResourceNotFoundException;
import com.digital.repository.ParentRepository;
import com.digital.repository.UserRepository;
import com.digital.servicei.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/parent")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ParentController {
    private final ParentService parentService;
    private final UserRepository userRepository;
    private final ParentRepository parentRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<ParentDashboardResponse> dashboard(Principal principal) {
        String loginName = principal.getName();

        // Fetch User by username or email
        User user = userRepository.findByUsername(loginName)
                .or(() -> userRepository.findByEmail(loginName))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loginName));

        // Fetch Parent linked to User
        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found for user: " + loginName));

        return ResponseEntity.ok(parentService.getDashboard(parent.getParentId()));
    }

    @GetMapping("/attendance/{studentId}")
    public ResponseEntity<List<Attendance>> attendance(Principal principal,
                                                       @PathVariable("studentId") Long studentRegId) {

        String loginName = principal.getName();

        // Fetch User by username or email
        User user = userRepository.findByUsername(loginName)
                .or(() -> userRepository.findByEmail(loginName))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loginName));

        // Fetch Parent linked to User
        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found for user: " + loginName));

        return ResponseEntity.ok(parentService.getAttendance(parent.getParentId(), studentRegId));
    }

    @GetMapping("/assignments")
    @PreAuthorize("hasRole('PARENT')")
    public ResponseEntity<?> getAssignments(Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found"));

        List<AssignmentResponse> assignments = parentService.getAssignmentsForChild(parent.getParentId());
        return ResponseEntity.ok(assignments);
    }



    @GetMapping("/exams/{studentId}")
    public ResponseEntity<List<ParentDashboardResponse.UpcomingExamDto>> exams(
            Principal principal,
            @PathVariable Long studentId) {

        String loginName = principal.getName(); // username/email

        // Fetch User entity
        User user = userRepository.findByUsername(loginName)
                .or(() -> userRepository.findByEmail(loginName))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loginName));

        // Fetch Parent linked to User
        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found for user: " + loginName));

        // Call service using correct parentId
        List<ParentDashboardResponse.UpcomingExamDto> exams = parentService.getExams(parent.getParentId(), studentId);

        return ResponseEntity.ok(exams);
    }

    @GetMapping("/results/{studentId}")
    public ResponseEntity<List<ResultResponse>> results(
            Principal principal,
            @PathVariable("studentId") Long studentRegId) {

        String loginName = principal.getName(); // username/email

        // Fetch User entity
        User user = userRepository.findByUsername(loginName)
                .or(() -> userRepository.findByEmail(loginName))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loginName));

        // Fetch Parent linked to User
        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found for user: " + loginName));

        //Call service using correct parentId, returns DTOs
        List<ResultResponse> responses = parentService.getResults(parent.getParentId(), studentRegId);

        return ResponseEntity.ok(responses);
    }


    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDto>> notifications(Principal principal) {
        String loginName = principal.getName(); // username/email

        // Fetch User entity
        User user = userRepository.findByUsername(loginName)
                .or(() -> userRepository.findByEmail(loginName))
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + loginName));

        // Fetch Parent linked to User
        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found for user: " + loginName));

        // Call service using correct parentId
        List<NotificationDto> notifications = parentService.getNotifications(parent.getParentId());

        return ResponseEntity.ok(notifications);
    }


//    @GetMapping("/payments/{studentId}")
//    public ResponseEntity<?> payments(Principal principal, @PathVariable Long studentId){
//        Long parentId = Long.valueOf(principal.getName());
//        return ResponseEntity.ok(parentService.getPayments(parentId, studentId));
//    }
}
